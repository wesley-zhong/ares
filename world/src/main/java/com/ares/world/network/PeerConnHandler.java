package com.ares.world.network;

import com.ares.core.annotation.MsgId;
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
public class PeerConnHandler implements AresController {
    @Autowired
    private PeerConn  peerConn;

    @Value("${area.id:100}")
    private int areaId;
    @Value("${spring.application.name}")
    private String appName;


    @MsgId(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_REQ_VALUE)
    public void innerHandShake(long pid, ProtoInner.InnerServerHandShakeReq innerLoginRequest) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        peerConn.addContext(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName(), aresTKcpContext);
        log.info("####  innerHandShake  from: {}  :{}  finish", aresTKcpContext, innerLoginRequest);

        ProtoInner.InnerMsgHeader innerMsgHeader = ProtoInner.InnerMsgHeader.newBuilder().setRoleId(pid).build();
        ProtoInner.InnerServerHandShakeRes response = ProtoInner.InnerServerHandShakeRes.newBuilder().setAreaId(areaId)
                .setServiceName(appName).build();
        AresPacket aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_RES_VALUE,innerMsgHeader,response);
        aresTKcpContext.send(aresPacket);
    }
}
