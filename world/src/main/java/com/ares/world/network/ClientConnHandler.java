package com.ares.world.network;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.bean.AresPacket;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;
import com.game.protoGen.ProtoInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ClientConnHandler implements AresController {
    @Autowired
    private PeerConn  peerConn;

    @Value("${area.id:100}")
    private int areaId;
    @Value("${spring.application.name}")
    private String appName;


    @CalledMsgId(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_REQ_VALUE)
    public void innerHandShake(ProtoInner.InnerServerHandShakeReq innerLoginRequest) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        peerConn.addContext(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName(), aresTKcpContext);
        log.info("####  innerHandShake  from: {}  :{}  finish", aresTKcpContext, innerLoginRequest);

        ProtoInner.InnerServerHandShakeRes response = ProtoInner.InnerServerHandShakeRes.newBuilder().setAreaId(areaId)
                .setServiceName(appName).build();
        AresPacket aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_RES_VALUE,response);
        aresTKcpContext.send(aresPacket);
    }
}
