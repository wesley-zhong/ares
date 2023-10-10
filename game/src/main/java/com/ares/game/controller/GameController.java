package com.ares.game.controller;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameController implements AresController {
    @Autowired
    private AresTcpClient aresTcpClient;
    @CalledMsgId(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE)
    public void loginRequest(ProtoInner.InnerGameLoginRequest innerLoginRequest) {

    }
}
