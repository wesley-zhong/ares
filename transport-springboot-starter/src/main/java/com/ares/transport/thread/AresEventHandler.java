package com.ares.transport.thread;


import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.transport.context.AresTKcpContextImplEx;
import com.lmax.disruptor.EventHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class AresEventHandler implements EventHandler<AresPacketEvent> {
    private final AresTcpHandler aresTpcHandler;

    public AresEventHandler(AresTcpHandler aresTpcHandler) {
        this.aresTpcHandler = aresTpcHandler;
    }

    @Override
    public void onEvent(AresPacketEvent event, long sequence, boolean endOfBatch) {
        AresTKcpContextImplEx aresPacketEx = event.getPacket();
        onPacket(aresPacketEx);
        clearEvent(event);
    }

    void onPacket(AresTKcpContextImplEx aresPacketEx) {
        try {
            if (aresPacketEx.getRcvPackage() != null) {
                doOnPacket(aresPacketEx);
            }
        } finally {
            clearPacket(aresPacketEx);
        }
    }

    private void doOnPacket(AresTKcpContextImplEx aresPacketEx) {
        AresPacket aresPacket = aresPacketEx.getRcvPackage();
        ChannelHandlerContext context = aresPacketEx.getCtx();
        if (context.isRemoved()) {
            log.error("XXXXXXXX ctx  is removed");
            return;
        }
        if (!context.channel().isActive()) {
            log.error("XXXXXXXX channel ={} is not validate ", context.channel().remoteAddress());
            return;
        }
        AresContextThreadLocal.cache(aresPacketEx);
        aresTpcHandler.handleMsgRcv(aresPacket);
    }


    private void clearPacket(AresTKcpContextImplEx aresPacketEx) {
        if (aresPacketEx == null) {
            return;
        }
        AresPacket aresPacket = aresPacketEx.getRcvPackage();
        if (aresPacket == null) {
            return;
        }
        aresPacket.release();
        aresPacketEx.setAresPacket(null);
    }

    private void clearEvent(AresPacketEvent event) {
        event.setPacket(null);
    }
}
