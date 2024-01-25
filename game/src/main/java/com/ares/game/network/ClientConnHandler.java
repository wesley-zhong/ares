package com.ares.game.network;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.game.DO.AccountDO;
import com.ares.game.service.PlayerRoleService;
import com.game.protoGen.ProtoInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ClientConnHandler implements AresController {
    @Autowired
    private PeerConn  peerConn;
    @CalledMsgId(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_VALUE)
    public void innerHandShake(ProtoInner.InnerServerHandShake innerLoginRequest) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        peerConn.addContext(innerLoginRequest.getAreaId(), innerLoginRequest.getServiceName(), aresTKcpContext);
        log.info("####  from: {} innerHandShake :{}  finish", aresTKcpContext, innerLoginRequest);
    }
}
