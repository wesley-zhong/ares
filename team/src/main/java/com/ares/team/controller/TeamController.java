package com.ares.team.controller;

import com.ares.core.annotation.MsgId;
import com.ares.core.service.AresController;
import com.ares.team.network.PeerConn;
import com.ares.team.service.PlayerService;
import com.ares.team.service.TeamPlayerMgr;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TeamController implements AresController {
    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamPlayerMgr teamPlayerMgr;
    @Autowired
    private PeerConn peerConn;

    @MsgId(ProtoCommon.ProtoCode.DIRECT_TO_WORLD_REQ_VALUE)
    public void directToTeamMsg(long pid, ProtoTask.DirectToWorldReq directToWorldReq) {
        log.info("XXXXXXXXXX  directToWorldMsg pid={}  body={} ", pid, directToWorldReq);
        ProtoTask.DirectToWorldRes fromWorld = ProtoTask.DirectToWorldRes.newBuilder()
                .setResBody("from world")
                .setSomeId(881)
                .setSomeIdAdd(9955599L).build();
        peerConn.routerToGame(pid, ProtoCommon.ProtoCode.DIRECT_TO_WORLD_RES_VALUE, fromWorld);
    }
}
