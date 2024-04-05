package com.ares.team.network;

import com.ares.core.annotation.MsgId;
import com.ares.core.bean.AresPacket;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;
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
    private PeerConn  peerConn;

    @Value("${area.id:0}")
    private int areaId;
    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private AresTcpClient aresTcpClient;

    @MsgId(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_RES_VALUE)
    public void innerHandShakeRes(long pid, ProtoInner.InnerServerHandShakeRes innerLoginRequest) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        peerConn.addContext(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName(), aresTKcpContext);
        log.info("#### innerHandShake  from: {}  Response :{}  finish", aresTKcpContext, innerLoginRequest);
        TcpConnServerInfo tcpConnServerInfo = aresTcpClient.getTcpConnServerInfo(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName());
        if(tcpConnServerInfo == null){
            log.error("server connect error  service name ={} areaId ={}",innerLoginRequest.getServiceName(), innerLoginRequest.getAreaId());
            return;
        }
        aresTKcpContext.cacheObj(tcpConnServerInfo);
    }
}
