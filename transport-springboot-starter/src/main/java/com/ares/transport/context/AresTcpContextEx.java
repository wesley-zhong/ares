package com.ares.transport.context;


import com.ares.core.bean.AresPacket;
import io.netty.channel.ChannelHandlerContext;


public class AresTcpContextEx extends AresTcpContext {
    public AresTcpContextEx(ChannelHandlerContext channelHandlerContext, AresPacket aresPacket) {
        super(channelHandlerContext);
        this.aresPacket = aresPacket;
    }


    public AresTcpContextEx(ChannelHandlerContext channelHandlerContext) {
        super(channelHandlerContext);
    }
    @Override
    public AresPacket getRcvPackage() {
        return aresPacket;
    }



    public void setAresPacket(AresPacket aresPacket) {
        this.aresPacket = aresPacket;
    }

    private AresPacket aresPacket;
}

