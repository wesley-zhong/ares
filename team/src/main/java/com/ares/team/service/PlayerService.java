package com.ares.team.service;

import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.timer.ScheduleService;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.team.bean.BeanTest;
import com.ares.team.network.PeerConn;
import com.ares.team.player.TeamPlayer;
import com.game.protoGen.ProtoInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlayerService {
    @Autowired
    private TeamPlayerMgr teamPlayerMgr;
    @Autowired
    private PeerConn peerConn;

    public void playerLogin(long roleId, ProtoInner.InnerLoginWorldRequest longinReq) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        ProtoInner.InnerWorldLoginResponse.Builder builder = ProtoInner.InnerWorldLoginResponse.newBuilder();
        builder.setRoleId(longinReq.getRoleId());
        ProtoInner.InnerWorldLoginResponse response = builder.build();
        TeamPlayer player = teamPlayerMgr.getPlayer(roleId);
        if(player == null){
            player=  teamPlayerMgr.crateWorldPlayer(aresTKcpContext, roleId);
        }
        player.setContext(aresTKcpContext);
      //  peerConn.sendToGame(roleId,ProtoInner.InnerProtoCode.INNER_TO_WORLD_LOGIN_RES_VALUE, response);
        player.send(ProtoInner.InnerProtoCode.INNER_TO_WORLD_LOGIN_RES_VALUE, response);


      //  log.info("PPPPPPPPPPPPPPPPPPPPP player login ={}", roleId);
        ScheduleService.INSTANCE.executeTimerTaskWithMS(this::timerTaskTest,new BeanTest(2211,"hello"),2000);
    }

    public void timerTaskTest(BeanTest beanTest){
        log.info("------ come from timer task {}", beanTest);
    }
}