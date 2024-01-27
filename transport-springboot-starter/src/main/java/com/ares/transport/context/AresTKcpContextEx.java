package com.ares.transport.context;


import com.ares.core.bean.AresPacket;
import io.netty.channel.ChannelHandlerContext;


public class AresTKcpContextEx extends AresTKcpContext {
    public AresTKcpContextEx(ChannelHandlerContext channelHandlerContext, AresPacket aresPacket) {
        super(channelHandlerContext);
        this.aresPacket = aresPacket;
    }


    public AresTKcpContextEx(ChannelHandlerContext channelHandlerContext) {
        super(channelHandlerContext);
    }
    @Override
    public AresPacket getRcvPackage() {
        return aresPacket;
    }
    @Override
    public void clearPackageData() {
        aresPacket = null;
    }



    public void setAresPacket(AresPacket aresPacket) {
        this.aresPacket = aresPacket;
    }

    private AresPacket aresPacket;
}

