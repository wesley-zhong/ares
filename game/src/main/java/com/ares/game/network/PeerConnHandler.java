package com.ares.game.network;

import com.ares.core.annotation.MsgId;
import com.ares.core.bean.AresPacket;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.bean.TcpConnServerInfo;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class PeerConnHandler implements AresController {
    @Autowired
    private PeerConn peerConn;

    @Value("${area.id:100}")
    private int areaId;
    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private AresTcpClient aresTcpClient;


    //as sever receive client handshake
    @MsgId(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_REQ_VALUE)
    public void innerHandShake(long id, ProtoInner.InnerServerHandShakeReq innerLoginRequest) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        peerConn.addContext(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName(), aresTKcpContext);
        log.info("####  from: {} innerHandShake :{}  finish", aresTKcpContext, innerLoginRequest);
        ProtoInner.InnerServerHandShakeRes response = ProtoInner.InnerServerHandShakeRes.newBuilder().setAreaId(areaId)
                .setServiceName(appName).build();
        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().setRoleId(id).build();
        AresPacket aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_RES_VALUE, header, response);
        aresTKcpContext.send(aresPacket);
        ServerNodeInfo serverNodeInfo = new ServerNodeInfo();
        serverNodeInfo.setAreaId(innerLoginRequest.getAreaId());
        serverNodeInfo.setServiceName(innerLoginRequest.getServiceName());
        serverNodeInfo.setServiceId(innerLoginRequest.getServiceId());
        TcpConnServerInfo tcpConnServerInfo = new TcpConnServerInfo(aresTKcpContext.getCtx().channel(), serverNodeInfo);
        aresTKcpContext.cacheObj(tcpConnServerInfo);
    }

    // as client receive from my handshake msg
    @MsgId(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_RES_VALUE)
    public void innerHandShakeRes(long id, ProtoInner.InnerServerHandShakeRes innerLoginRequest) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        peerConn.addContext(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName(), aresTKcpContext);
        log.info("####  innerHandShake from: {}  Response :{}  finish", aresTKcpContext, innerLoginRequest);
        TcpConnServerInfo tcpConnServerInfo = aresTcpClient.getTcpConnServerInfo(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName());
        if (tcpConnServerInfo == null) {
            log.error("server connect error  service name ={} areaId ={}", innerLoginRequest.getServiceName(), innerLoginRequest.getAreaId());
            return;
        }
        aresTKcpContext.cacheObj(tcpConnServerInfo);
    }
}
