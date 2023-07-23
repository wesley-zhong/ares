package com.ares.transport.encode;

import com.ares.core.bean.AresPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class AresPacketMsgEncoder extends MessageToByteEncoder<AresPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, AresPacket msg, ByteBuf out) {
        byte[] sendBody = msg.bodyEncode();
        int msgId = msg.getMsgId();
        int msgLen = 2;
        if (sendBody != null) {
            msgLen += sendBody.length;
        }
        out.writeInt(msgLen);
        out.writeShort(msgId);
        if (sendBody != null) {
            out.writeBytes(sendBody);
        }
        msg.release();
    }
}
