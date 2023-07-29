package com.ares.client.controller;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.game.proto.ProtoCommon;
import com.game.proto.ProtoTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class CommController implements AresController {
    @CalledMsgId(ProtoCommon.ProtoCode.LOGIN_RESPONSE_VALUE)
    public void userLoginResponse(ProtoTask.LoginResponse response){
        log.info("------login response ={}", response);

    }
}
