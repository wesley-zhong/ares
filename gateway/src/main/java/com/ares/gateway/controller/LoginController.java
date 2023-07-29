package com.ares.gateway.controller;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;

import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoTask;
import org.springframework.stereotype.Component;

@Component
public class LoginController implements AresController {

    @CalledMsgId(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE)
    public void loginRequest(ProtoTask.LoginRequest loginRequest){
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();


    }

}
