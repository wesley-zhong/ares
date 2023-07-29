package com.ares.gateway.controller;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.bean.AresPacket;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;

import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginController implements AresController {

    @CalledMsgId(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE)
    public void loginRequest(ProtoTask.LoginRequest loginRequest){
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        log.info("-------receive from ={} msg ={}", aresTKcpContext, loginRequest);

        ProtoTask.LoginResponse.Builder response = ProtoTask.LoginResponse.newBuilder()
                .setErrorCode(0)
                .setRoleId(123)
                .setServerTime(System.currentTimeMillis());
        aresTKcpContext.send(AresPacket.create(ProtoCommon.ProtoCode.LOGIN_RESPONSE_VALUE, response.build()));

    }

}
