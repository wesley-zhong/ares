package com.ares.gateway.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.bean.AresRpcMethod;
import com.ares.core.exception.AresBaseException;
import com.ares.core.service.ServiceMgr;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.bean.TcpConnServerInfo;
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
    @Autowired
    private AresTcpClient aresTcpClient;
    @Value("${spring.application.name}")
    private String appName;


    @Value("${area.id:100}")
    private int areaId;
    @Override
    public void handleMsgRcv(AresPacket aresPacket) {
        int length = 0;
        try {
            AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
            AresRpcMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());
            //come from peer server node
            long roleId = 0;
            if(aresTKcpContext.getCacheObj() instanceof TcpConnServerInfo
                    || aresPacket.getMsgId() == ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_RES_VALUE){
                int headerLen = aresPacket.getRecvByteBuf().readShort();
                if (headerLen > 0) {
                    ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), headerLen));
                    roleId = header.getRoleId();
                }
                if (calledMethod == null) {
                    directSendToClient(roleId, aresPacket);
                    return;
                }
            }

            length = aresPacket.getRecvByteBuf().readableBytes();
            Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
            calledMethod.getAresServiceProxy().callMethod(calledMethod, roleId, paraObj);
        } catch (AresBaseException e) {
            log.error("===error  length ={} msgId={} ", length, aresPacket.getMsgId(), e);
        } catch (Throwable e) {
            log.error("==error length ={} msgId ={}  ", length, aresPacket.getMsgId(), e);
        }
    }


    private void directSendToClient(long roleId, AresPacket aresPacket){

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
