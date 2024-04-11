package com.ares.game.player;

import com.ares.core.tcp.AresTKcpContext;
import com.ares.game.DO.RoleDO;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GamePlayer {
    private long pid;

    private RoleDO roleDO;
    public GamePlayer(long id){
        this.pid = id;
    }
    public long getPid(){
        return roleDO.getPid();
    }
}
