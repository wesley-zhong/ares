package com.ares.game.controller;

import com.ares.common.bean.ServerType;
import com.ares.core.annotation.MsgId;
import com.ares.core.service.AresController;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.timer.ScheduleService;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.game.bean.TimerBeanTest;
import com.ares.game.network.PeerConn;
import com.ares.game.player.GamePlayer;
import com.ares.game.service.PlayerRoleService;
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
    private PlayerRoleService playerRoleService;
    @Autowired
    private PeerConn peerConn;

    @MsgId(ProtoInner.InnerProtoCode.INNER_TO_GAME_LOGIN_REQ_VALUE)
    public void gameInnerLoginRequest(long pid, ProtoInner.InnerGameLoginRequest gameInnerLoginRequest) {
        /**
         * do some logic
         */
        log.info("======== gameInnerLoginRequest  ={}", gameInnerLoginRequest);
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        GamePlayer player = playerRoleService.getPlayer(gameInnerLoginRequest.getRoleId());
        if (player == null) {
            player = playerRoleService.createGamePlayer(gameInnerLoginRequest.getRoleId(), "hello");
        }
        peerConn.recordPlayerFromContext(ServerType.GATEWAY, gameInnerLoginRequest.getRoleId(), aresTKcpContext.getCtx());
        sendPlayerLoginResponse(gameInnerLoginRequest.getRoleId());
    }


    private void sendPlayerLoginResponse(long pid) {
        GamePlayer player = playerRoleService.getPlayer(pid);
        if (player == null) {
            log.error(" pid={} not found", pid);
            return;
        }
        ProtoInner.InnerGameLoginResponse innerGameLoginRes = ProtoInner.InnerGameLoginResponse.newBuilder()
                .setRoleId(pid).build();
        peerConn.sendGateWayMsg(pid, ProtoInner.InnerProtoCode.INNER_TO_GAME_LOGIN_RES_VALUE, innerGameLoginRes);
    }


    @MsgId(ProtoInner.InnerProtoCode.INNER_PLAYER_DISCONNECT_REQ_VALUE)
    public void playerDisconnected(long pid, ProtoInner.InnerPlayerDisconnectRequest innerLoginRequest) {
        log.info("======== playerDisconnected  ={}", innerLoginRequest);
    }

    @MsgId(ProtoCommon.ProtoCode.PERFORMANCE_TEST_REQ_VALUE)
    public void performanceTest(long pid, ProtoTask.PerformanceTestReq req) {
        GamePlayer player = playerRoleService.getPlayer(pid);
        if (player == null) {
            log.error(" pid ={} not found", pid);
            return;
        }
        log.info("-----performanceTest  pid ={} body={} ", pid, req);
        ProtoTask.PerformanceTestRes performanceBOyd = ProtoTask.PerformanceTestRes.newBuilder().setResBody("performanceBody").setSomeId(44444).build();
        peerConn.sendGateWayMsg(pid, ProtoCommon.ProtoCode.PERFORMANCE_TEST_RES_VALUE, performanceBOyd);

        //for test
        playerRoleService.asynUpdateTest(player.getRoleDO());

        //for timer test
        log.info("==================================== start timer call begin");
        TimerBeanTest  timerBeanTest = new TimerBeanTest();
        timerBeanTest.msg ="timerTest";
        ScheduleService.INSTANCE.executeTimerTaskWithMS(playerRoleService::onTimerTest,timerBeanTest,3000L);
    }
}
