package com.ares.team.controller;

import com.ares.core.annotation.MsgId;
import com.ares.core.service.AresController;
import com.ares.team.network.PeerConn;
import com.ares.team.service.PlayerService;
import com.ares.team.service.WorldPlayerMgr;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorldController implements AresController {
    @Autowired
    private PlayerService playerService;

    @Autowired
    private WorldPlayerMgr worldPlayerMgr;
    @Autowired
    private PeerConn peerConn;

    @MsgId(ProtoInner.InnerProtoCode.INNER_TO_WORLD_LOGIN_REQ_VALUE)
    public void playerWorldLoginRequest(long pid, ProtoInner.InnerLoginWorldRequest innerLoginRequest) {
        log.info("  loginRequest  = {}", innerLoginRequest);
        playerService.playerLogin(pid, innerLoginRequest);
    }

    @MsgId(ProtoInner.InnerProtoCode.INNER_PLAYER_DISCONNECT_REQ_VALUE)
    public void playerDisconnected(long pid, ProtoInner.InnerPlayerDisconnectRequest innerLoginRequest) {
        log.info("======== gameLoginRequest  ={}", innerLoginRequest);
    }

    @MsgId(ProtoCommon.ProtoCode.DIRECT_TO_WORLD_REQ_VALUE)
    public void directToWorldMsg(long pid, ProtoTask.DirectToWorldReq directToWorldReq) {
        log.info("XXXXXXXXXX  directToWorldMsg pid={}  body={} ", pid, directToWorldReq);
        ProtoTask.DirectToWorldRes fromWorld = ProtoTask.DirectToWorldRes.newBuilder()
                .setResBody("from world")
                .setSomeId(881)
                .setSomeIdAdd(9955599L).build();
        peerConn.sendToGame(pid, ProtoCommon.ProtoCode.DIRECT_TO_WORLD_RES_VALUE, fromWorld);
    }
}
