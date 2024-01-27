package com.ares.client.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.bean.AresRpcMethod;
import com.ares.core.exception.AresBaseException;
import com.ares.core.service.ServiceMgr;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoInner;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
public class AresTcpHandlerImpl implements AresTcpHandler {

    @Autowired
    private ServiceMgr serviceMgr;
    
    @Override
    public void handleMsgRcv(AresPacket aresPacket) {
        int length = 0;
        try {
            AresRpcMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());
            if (calledMethod == null) {
               // tcpNetWorkHandler.handleMsgRcv(aresPacket);
                return;
            }
            aresPacket.getRecvByteBuf().skipBytes(6);
            length = aresPacket.getRecvByteBuf().readableBytes();
            Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
            calledMethod.getAresServiceProxy().callMethod(calledMethod, paraObj);
        } catch (AresBaseException e) {
            log.error("===error  length ={} msgId={} ", length, aresPacket.getMsgId(), e);
        } catch (Throwable e) {
            log.error("==error length ={} msgId ={}  ", length, aresPacket.getMsgId(), e);
        }
    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {
        log.info("----- {} connected", aresTKcpContext);

    }

    @Override
    public void onClientConnected(AresTKcpContext aresTKcpContext) {
        log.info("---onClientConnected ={} ", aresTKcpContext);
    }

    @Override
    public void onClientClosed(AresTKcpContext aresTKcpContext) {
        log.info("-----onClientClosed={} ", aresTKcpContext);
    }

    @Override
    public boolean isChannelValidate(AresTKcpContext aresTKcpContext) {
        return true;
    }


    @Override
    public void onServerClosed(Channel aresTKcpContext) {

    }
}