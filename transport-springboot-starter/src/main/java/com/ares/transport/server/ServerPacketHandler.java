package com.ares.transport.server;


import com.ares.core.tcp.AresTcpHandler;
import com.ares.transport.consts.FMsgId;
import com.ares.core.bean.AresPacket;
import com.ares.transport.context.AresTKcpContextEx;
import com.ares.transport.thread.PackageProcessThreadPool;
import com.ares.transport.thread.PackageProcessThreadPoolGroup;
import com.ares.transport.utils.AresPacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerPacketHandler extends ChannelInboundHandlerAdapter {
    private static AresTcpHandler aresRpcHandler;
    private static PackageProcessThreadPoolGroup s_serverRpcProcessThreadPoolGroup;
    private final PackageProcessThreadPool serverRpcProcessThreadPool;
    private int hearBeatCount;
    private int curIgnoreReadIdleCount;
    private static final int MAX_NO_CHECK_COUNT = 64;
    private final int totalIgnoreReadIdleCount;
    private final static int MSG_ID_OFFSET = 4;


    public ServerPacketHandler(AresTcpHandler aresRpc, PackageProcessThreadPoolGroup packageProcessThreadPoolGroup, int totalIgnoreReadIdleCount) {
        if (aresRpcHandler == null || s_serverRpcProcessThreadPoolGroup == null) {
            aresRpcHandler = aresRpc;
            s_serverRpcProcessThreadPoolGroup = packageProcessThreadPoolGroup;
        }
        serverRpcProcessThreadPool = s_serverRpcProcessThreadPoolGroup.getThreadPoolByThreadId();
        hearBeatCount = 0;
        curIgnoreReadIdleCount = 0;
        this.totalIgnoreReadIdleCount = totalIgnoreReadIdleCount;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object in) {
        ByteBuf body = (ByteBuf) in;
        int msgId = body.readUnsignedShort();
        AresTKcpContextEx aresTcpContextEx = AresPacketUtils.parseAresPacket(ctx, body, msgId);
        processAresPacket(aresTcpContextEx, ctx);
    }

    private void processAresPacket(AresTKcpContextEx aresMsgEx, ChannelHandlerContext ctx) {
        AresPacket aresPacket = aresMsgEx.getRcvPackage();
        boolean ret = checkValid(aresMsgEx, aresPacket.getMsgId());
        if (!ret) {
            aresPacket.release();
            return;
        }
        if (aresPacket.getMsgId() == FMsgId.PONG) {
            aresPacket.release();
            sendPing(ctx);
            return;
        }
        serverRpcProcessThreadPool.execute(aresMsgEx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        AresTKcpContextEx aresTcpContextEx = new AresTKcpContextEx(ctx);
        aresRpcHandler.onClientConnected(aresTcpContextEx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                curIgnoreReadIdleCount++;
                log.info("==================READER_IDLE idle close ServerIdleStateTrigger {} count={}", ctx.channel().remoteAddress(), curIgnoreReadIdleCount);
                if (curIgnoreReadIdleCount > totalIgnoreReadIdleCount) {
                    log.info("======= close socket={}", ctx.channel().remoteAddress());
                    ctx.close();
                }
            }
            return;
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            log.error("{} ------  socket channelInactive", ctx.channel().remoteAddress());
            aresRpcHandler.onServerClosed(ctx.channel());
            ctx.close();
        } finally {
            try {
                super.channelInactive(ctx);
            } catch (Exception e) {
                log.error(" error", e);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("{} ------  socket error, case={}", ctx.channel().remoteAddress(), cause.getMessage());
        ctx.close();
    }

    private void sendPing(ChannelHandlerContext ctx) {
        AresPacket ping = AresPacket.create((short) FMsgId.PING);
        ctx.writeAndFlush(ping);
    }

    private boolean checkValid(AresTKcpContextEx aresMsgEx, int msgId) {
        if (msgId == FMsgId.PONG) {
            if (hearBeatCount < MAX_NO_CHECK_COUNT) {
                hearBeatCount++;
                return true;
            }
            curIgnoreReadIdleCount = totalIgnoreReadIdleCount;
        }

        return aresRpcHandler.isChannelValidate(aresMsgEx);
    }
}