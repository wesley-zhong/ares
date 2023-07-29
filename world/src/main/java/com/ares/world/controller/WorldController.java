package com.ares.world.controller;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import org.springframework.stereotype.Component;

@Component
public class WorldController implements AresController {
    @CalledMsgId(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE)
    public void loginRequest(ProtoInner.InnerLoginRequest innerLoginRequest) {

    }
}
