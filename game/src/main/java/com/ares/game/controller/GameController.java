package com.ares.game.controller;

import com.ares.core.annotation.CalledMsgId;
import com.ares.core.service.AresController;
import com.ares.game.service.PlayerRoleService;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GameController implements AresController {
    @Autowired
  private PlayerRoleService  playerRoleService;
    @CalledMsgId(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE)
    public void loginRequest(ProtoTask.LoginRequest innerLoginRequest) {
        log.info("======== loginRequest  ={}", innerLoginRequest);
        playerRoleService.getPlayer(innerLoginRequest.getRoleId());
    }

    @CalledMsgId(ProtoInner.InnerProtoCode.INNER_TO_WORLD_LOGIN_RES_VALUE)
    public void worldLoginResponse(ProtoInner.InnerWorldLoginResponse innerLoginRequest) {
        log.info("======== gameLoginRequest  ={}", innerLoginRequest);
    }


    @CalledMsgId(ProtoInner.InnerProtoCode.INNER_PLAYER_DISCONNECT_REQ_VALUE)
    public void playerDisconnected(ProtoInner.InnerPlayerDisconnectRequest innerLoginRequest) {
        log.info("======== gameLoginRequest  ={}", innerLoginRequest);
    }


}
