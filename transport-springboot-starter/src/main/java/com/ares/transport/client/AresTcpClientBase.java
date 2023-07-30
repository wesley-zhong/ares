package com.ares.transport.client;

import com.ares.common.bean.ServerInfo;
import com.ares.core.bean.AresPacket;
import com.ares.transport.bean.TcpConnServerInfo;
import com.ares.core.thread.AresThreadFactory;
import com.google.protobuf.Message;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;


@Slf4j
public abstract class AresTcpClientBase implements AresTcpClient {
    private final ThreadFactory threadFactory = new AresThreadFactory("a-c-t");
    private final List<ServerInfo> serverInfos = new CopyOnWriteArrayList<>();
    protected final Map<Integer, TcpConnServerInfo> tcpConnServerInfoMap = new ConcurrentHashMap<>();

    public AresTcpClientBase(List<ServerInfo> serverInfos) {
        this.serverInfos.addAll(serverInfos);
    }

    @Override
    public void init() {
        threadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                log.info("---- start  tcp client thread ------");
                try {
                    while (true) {
                        connectCheck();
                        Thread.sleep(5000);
                    }
                } catch (Exception e) {
                    log.error("-----conn error", e);
                }
            }
        }).start();
    }

    private void connectCheck() {
        for (ServerInfo serverInfo : serverInfos) {
            TcpConnServerInfo tcpConnServerInfo = tcpConnServerInfoMap.get(serverInfo.getId());
            if (tcpConnServerInfo == null) {
                tcpConnServerInfo = new TcpConnServerInfo();
                tcpConnServerInfo.setServerInfo(serverInfo);
                Channel channel = connect(serverInfo);
                if(channel == null){
                    continue;
                }
                tcpConnServerInfo.setChannel(channel);
                tcpConnServerInfoMap.put(serverInfo.getId(), tcpConnServerInfo);
                log.info("----connect ={} finish", serverInfo);
                continue;
            }
            if (tcpConnServerInfo.getChannel().isActive()) {
                continue;
            }
            Channel channel = connect(serverInfo);
            log.info("----connect1 ={}  finished", serverInfo);
            tcpConnServerInfo.setChannel(channel);
            tcpConnServerInfoMap.put(serverInfo.getId(), tcpConnServerInfo);
        }
    }


    public void addServerInfo(ServerInfo serverInfo) {
        serverInfos.add(serverInfo);
    }

    public void delServerInfo(ServerInfo serverInfo) {
        serverInfos.remove(serverInfo);
    }


    public void send(Channel channel, int msgId, Message message) {
        AresPacket msgPack = AresPacket.create(msgId, message);
        channel.writeAndFlush(msgPack);
    }

    public void send(Channel channel, AresPacket aresPacket) {
        channel.writeAndFlush(aresPacket);
    }

    public void send(Channel channel, AresPacket... packets) {
        for (AresPacket aresPacket : packets) {
            channel.write(aresPacket);
        }
        channel.flush();
    }

    protected abstract Channel connect(ServerInfo serverInfo);

}
