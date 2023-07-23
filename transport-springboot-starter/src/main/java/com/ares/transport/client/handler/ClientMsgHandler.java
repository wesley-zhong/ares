package com.ares.transport.client.handler;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.transport.consts.FMsgId;
import com.ares.transport.context.AresTcpContextEx;
import com.ares.transport.utils.AresPacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ClientMsgHandler extends ChannelInboundHandlerAdapter {

    private final AresTcpHandler aresTcpHandler;

    public ClientMsgHandler(AresTcpHandler aresTcpHandler) {
        this.aresTcpHandler = aresTcpHandler;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf body = (ByteBuf) msg;
        short msgId = body.readShort();
        AresTcpContextEx aresTcpContextEx = AresPacketUtils.parseAresPacket(ctx, body, msgId);
        processAresPacket(aresTcpContextEx);
    }

    private void processAresPacket(AresTcpContextEx aresPacketEx) {
        AresPacket arePacket = aresPacketEx.getRcvPackage();
        if (arePacket != null) {
            if (arePacket.getMsgId() == FMsgId.PING) {
                arePacket.release();
                return;
            }
            AresContextThreadLocal.cache(aresPacketEx);
            aresTcpHandler.handleMsgRcv(arePacket);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("ip ={} connected  sucess！！", ctx.channel().remoteAddress());
        aresTcpHandler.onServerConnected(ctx.channel());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error(" {} connect lost ", ctx.channel().remoteAddress());
         aresTcpHandler.onServerClosed(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception { // (4)
        log.error("--------------   connect exceptionCaught  ip = {} ", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }

}
