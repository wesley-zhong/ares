package com.ares.gateway.controller;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.game.proto.ProtoCommon;
import com.game.proto.ProtoTask;
import org.springframework.stereotype.Component;

@Component
public class LoginController implements AresController {

    @CalledMsgId(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE)
    public void loginRequest(ProtoTask.LoginRequest loginRequest){

    }

}
