package com.ares.transport.utils;


import com.ares.core.bean.AresPacket;
import com.ares.transport.context.AresTcpContextEx;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AresPacketUtils {
    public static AresTcpContextEx parseAresPacket(ChannelHandlerContext ctx, ByteBuf in, int msgId) {
        AresPacket aresPacket = AresPacket.create(msgId);
        aresPacket.setRecvByteBuf(in);
        return new AresTcpContextEx(ctx, aresPacket);
    }

}
