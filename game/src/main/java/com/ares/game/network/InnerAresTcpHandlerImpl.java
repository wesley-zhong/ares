package com.ares.game.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.bean.AresRpcMethod;
import com.ares.core.exception.AresBaseException;
import com.ares.core.service.ServiceMgr;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.tcp.AresTcpHandlerBase;
import com.ares.core.tcp.TcpNetWorkHandler;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoInner;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
public class InnerAresTcpHandlerImpl implements AresTcpHandler{
    @Autowired
    protected ServiceMgr serviceMgr;

    @Autowired
    private AresTcpClient aresTcpClient;
    @Value("${spring.application.name}")
    private String appName;


    @Value("${area.id:100}")
    private int areaId;


    protected static final String UTF8 = "UTF-8";
    @Override
    public void handleMsgRcv(AresPacket aresPacket) {
        int length = 0;
        try {
            AresRpcMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());
            if (calledMethod == null) {
               // tcpNetWorkHandler.handleMsgRcv(aresPacket);
                return;
            }
            int headerLen = aresPacket.getRecvByteBuf().readShort();
            long pid = 0;
            if (headerLen > 0) {
                ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), headerLen));
                pid = header.getRoleId();
            }

            length = aresPacket.getRecvByteBuf().readableBytes();
            Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
            calledMethod.getAresServiceProxy().callMethod(calledMethod, pid, paraObj);
        } catch (AresBaseException e) {
            log.error("===error  length ={} msgId={} ", length, aresPacket.getMsgId(), e);
        } catch (Throwable e) {
            log.error("==error length ={} msgId ={}  ", length, aresPacket.getMsgId(), e);
        }
    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {
        ProtoInner.InnerServerHandShakeReq handleShake = ProtoInner.InnerServerHandShakeReq.newBuilder()
                .setAreaId(areaId)
                .setServiceName(appName).build();

        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().build();
        AresPacket  aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_REQ_VALUE,header, handleShake);
        aresTKcpContext.writeAndFlush(aresPacket);
        log.info("###### send to {} handshake msg: {}",aresTKcpContext, handleShake);
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
