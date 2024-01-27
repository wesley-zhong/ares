package com.ares.gateway.controller;

import com.ares.core.annotation.MsgId;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;

import com.ares.gateway.bean.PlayerSession;
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

    @MsgId(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE)
    public void loginRequest(long roleId, ProtoTask.LoginRequest loginRequest) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        log.info("-------receive from ={} msg ={}", aresTKcpContext, loginRequest);
        sessionService.loginRequest(aresTKcpContext, loginRequest);
    }

    @MsgId(ProtoInner.InnerProtoCode.INNER_TO_GAME_LOGIN_RES_VALUE)
    public void onGameLoginRes(long roleId, ProtoInner.InnerGameLoginResponse loginResponse) {
        log.info(" INNER_TO_GAME_LOGIN_RES_VALUE :{} ",loginResponse);

        sessionService.loginSuccess(loginResponse);
        ProtoTask.LoginResponse response = ProtoTask.LoginResponse.newBuilder()
                .setErrorCode(0)
                .setRoleId(loginResponse.getRoleId())
                .setServerTime(System.currentTimeMillis()).build();
        sessionService.sendPlayerMsg(loginResponse.getRoleId(),ProtoCommon.ProtoCode.LOGIN_RESPONSE_VALUE, response );
    }
}
