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
        byte[] sendHeader = msg.headerEncode();
        //-----4 bytes msg len | ---2 bytes msgId--|  2 bytes header len----| header |   body|

        int msgId = msg.getMsgId();
        int msgLen = 2;
        if(sendHeader != null){
            msgLen +=2;
            msgLen += sendHeader.length;
        }

        if (sendBody != null) {
            msgLen += sendBody.length;
        }
        out.writeInt(msgLen);
        out.writeShort(msgId);
        if(sendHeader != null){
            out.writeShort(sendHeader.length);
            out.writeBytes(sendHeader);
        }
        if (sendBody != null) {
            out.writeBytes(sendBody);
        }
        msg.release();
    }
}
