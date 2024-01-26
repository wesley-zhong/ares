package com.ares.game.controller;

import com.ares.common.bean.ServerType;
import com.ares.core.annotation.MsgId;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.game.network.PeerConn;
import com.ares.game.network.WorldServerClientTransfer;
import com.ares.game.player.GamePlayer;
import com.ares.game.service.PlayerRoleService;
import com.game.protoGen.ProtoInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GameController implements AresController {
    @Autowired
    private PlayerRoleService playerRoleService;
    @Autowired
    private PeerConn peerConn;
    @Autowired
    private WorldServerClientTransfer worldServerClientTransfer;

    @MsgId(ProtoInner.InnerProtoCode.INNER_TO_GAME_LOGIN_REQ_VALUE)
    public void gameInnerLoginRequest(long pid, ProtoInner.InnerGameLoginRequest gameInnerLoginRequest) {
        /**
         * do some check
         */

        log.info("======== gameInnerLoginRequest  ={}", gameInnerLoginRequest);

        GamePlayer player = playerRoleService.getPlayer(gameInnerLoginRequest.getRoleId());
        if(player == null){
            AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
            GamePlayer gamePlayer = playerRoleService.createGamePlayer(aresTKcpContext.getCtx(), "hello");
        }
        ProtoInner.InnerLoginWorldRequest innerRequest = ProtoInner.InnerLoginWorldRequest.newBuilder()
                .setRoleId(gameInnerLoginRequest.getRoleId()).build();
        worldServerClientTransfer.sendMsg(ProtoInner.InnerProtoCode.INNER_TO_WORLD_LOGIN_REQ_VALUE,innerRequest);
    }

    @MsgId(ProtoInner.InnerProtoCode.INNER_TO_WORLD_LOGIN_RES_VALUE)
    public void worldLoginResponse(long pid, ProtoInner.InnerWorldLoginResponse worldLoginResponse) {
        log.info("======== worldLoginResponse  ={}", worldLoginResponse);
        ProtoInner.InnerGameLoginResponse innerGameLoginRes = ProtoInner.InnerGameLoginResponse.newBuilder()
                .setRoleId(worldLoginResponse.getRoleId()).build();
        log.info("======== send INNER_TO_GAME_LOGIN_RES_VALUE   ={}", innerGameLoginRes);
        peerConn.send(ServerType.GATEWAY,ProtoInner.InnerProtoCode.INNER_TO_GAME_LOGIN_RES_VALUE,innerGameLoginRes);
    }


    @MsgId(ProtoInner.InnerProtoCode.INNER_PLAYER_DISCONNECT_REQ_VALUE)
    public void playerDisconnected(long pid, ProtoInner.InnerPlayerDisconnectRequest innerLoginRequest) {
        log.info("======== gameLoginRequest  ={}", innerLoginRequest);
    }
}
