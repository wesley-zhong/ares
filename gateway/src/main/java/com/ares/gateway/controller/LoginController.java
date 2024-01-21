package com.ares.gateway.controller;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;

import com.ares.gateway.service.SessionService;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginController implements AresController {
    @Autowired
    private SessionService sessionService;


    @CalledMsgId(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE)
    public void loginRequest(ProtoTask.LoginRequest loginRequest) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        log.info("-------receive from ={} msg ={}", aresTKcpContext, loginRequest);
        sessionService.loginRequest(aresTKcpContext, loginRequest);
    }

    @CalledMsgId(ProtoInner.InnerProtoCode.INNER_TO_GAME_LOGIN_REQ_VALUE)
    public void onGameLoginRes(ProtoTask.LoginResponse loginResponse) {

    }

}
