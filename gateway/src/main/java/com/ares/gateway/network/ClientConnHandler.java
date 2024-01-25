package com.ares.gateway.network;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.transport.bean.TcpConnServerInfo;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ClientConnHandler implements AresController {
    @Autowired
    private PeerConn  peerConn;
    @Autowired
    private AresTcpClient aresTcpClient;


    @CalledMsgId(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_RES_VALUE)
    public void innerHandShakeRes(ProtoInner.InnerServerHandShakeRes innerLoginRequest) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        peerConn.addContext(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName(), aresTKcpContext);
        log.info("####  from: {} innerHandShake Response :{}  finish", aresTKcpContext, innerLoginRequest);
        TcpConnServerInfo tcpConnServerInfo = aresTcpClient.getTcpConnServerInfo(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName());
        if(tcpConnServerInfo == null){
            log.error("server connect error  service name ={} areaId ={}",innerLoginRequest.getServiceName(), innerLoginRequest.getAreaId());
            return;
        }
        aresTKcpContext.cacheObj(tcpConnServerInfo);
    }
}
