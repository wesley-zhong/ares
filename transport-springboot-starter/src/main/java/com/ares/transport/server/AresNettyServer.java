package com.ares.transport.server;

import com.ares.core.tcp.AresTcpHandler;
import com.ares.transport.decoder.AresBasedFrameDecoder;
import com.ares.transport.encode.AresPacketMsgEncoder;
import com.ares.core.thread.LogicProcessThreadPoolGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
public class AresNettyServer implements InitializingBean {

    @Autowired
    private AresTcpHandler aresRpcHandler;

    @Value("${server.port:8080}")
    private int port;

    @Value("${tcp.offset:0}")
    private int tcpOffset;

    @Value("${packet.limit:524288}")//512K
    private int packetLimit;

    @Value("${use_linux:true}")
    private boolean isUseLinux;

    @Value("${reuse_port:false}")
    private boolean reusePort;
    @Value("${asyn.logic_thread.count:0}")
    private int asynLogicThreadCount; // io thread and logic to separator

    @Value(("${tcp.server.heartBeat.time:22000}"))
    private long tcpServerHeartBeatTime;
    @Value("${first.total.ignore.read.idle.count:0}")
    private int firstTotalIgnoreReadIdleCount;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    private final List<Channel> bindChannels = new ArrayList<>();

    private void runNettyServer() throws Exception {
        log.info("#####isUseEpoll:{}", useLinux());
        ServerBootstrap b = new ServerBootstrap();
        int cpuNum = Runtime.getRuntime().availableProcessors();
        int eventCount = cpuNum * 2;//2 * cpuNum
        if (useLinux()) {
            bossGroup = new EpollEventLoopGroup(cpuNum);
            workerGroup = new EpollEventLoopGroup();
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(4);
        }

        //LogicProcessThreadPoolGroup processThreadPoolGroup = LogicProcessThreadPoolGroup.create(eventCount, aresRpcHandler, asynLogicThreadCount);
        b.group(bossGroup, workerGroup)
                .channel(useLinux() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(tcpServerHeartBeatTime, 0, 0, TimeUnit.MILLISECONDS))
                                .addLast(new ChannelTrafficShapingHandler(0, packetLimit))
                                .addLast(new AresBasedFrameDecoder())
                                .addLast(new AresPacketMsgEncoder())
                                .addLast(new ServerPacketHandler(aresRpcHandler, firstTotalIgnoreReadIdleCount));
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                        log.error("socket ={} =-----------error", ctx, cause);
                        ctx.close();
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_SNDBUF, 512 * 1024)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(512 * 1024, 1024 * 1024))
                .childOption(ChannelOption.SO_RCVBUF, 512 * 1024)
                .childOption(ChannelOption.TCP_NODELAY, true);

        int newPort = port + tcpOffset;
        if (reusePort) {
            b.option(EpollChannelOption.SO_REUSEPORT, true);
            b.option(EpollChannelOption.SO_REUSEADDR, true);
            for (int i = 0; i < cpuNum; ++i) {
                Channel bindChannel = b.bind(newPort).sync().channel();
                log.info("bind port ={} success", newPort);
                bindChannels.add(bindChannel);
            }
        } else {
            Channel bindChannel = b.bind(newPort).sync().channel();
            bindChannels.add(bindChannel);
            log.info("bind port ={} success", newPort);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                destroy();
            }
        }));
        log.info("##################  start tcp  server : {} success", newPort);
    }

    private boolean useLinux() {
        if (isUseLinux) {
            return Epoll.isAvailable();
        }
        return false;
    }

    private void destroy() {
        try {
            for (Channel bindChannel : bindChannels) {
                bindChannel.close().sync();
            }
           // LogicProcessThreadPoolGroup.INSTANCE.shutDown();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("-XXXXXXXXXXXXXXXXXXXXXXXXXXXX  stop ares netty server success");
        } catch (Exception e) {
            log.error("stop ares netty server failed", e);
        }
    }

    public void afterPropertiesSet() throws Exception {
        runNettyServer();
    }
}
