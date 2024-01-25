package com.ares.world.controller;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.ares.world.service.PlayerService;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorldController implements AresController {
    @Autowired
    private PlayerService playerService;
    @CalledMsgId(ProtoInner.InnerProtoCode.INNER_TO_WORLD_LOGIN_REQ_VALUE)
    public void playerWorldLoginRequest(ProtoInner.InnerLoginWorldRequest innerLoginRequest) {
        log.info("  loginRequest  = {}", innerLoginRequest);
        playerService.playerLogin(innerLoginRequest);
    }

    @CalledMsgId(ProtoInner.InnerProtoCode.INNER_PLAYER_DISCONNECT_REQ_VALUE)
    public void playerDisconnected(ProtoInner.InnerPlayerDisconnectRequest innerLoginRequest) {
        log.info("======== gameLoginRequest  ={}", innerLoginRequest);
    }
}
