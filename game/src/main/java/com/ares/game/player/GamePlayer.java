package com.ares.game.player;

import com.ares.core.tcp.AresTKcpContext;
import com.ares.game.DO.RoleDO;
import com.ares.game.network.PeerTransfer;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GamePlayer extends PeerTransfer {

    private RoleDO roleDO;

    public GamePlayer(long pid){
        super(pid);
    }
    public GamePlayer(AresTKcpContext context,long pid) {
        super(context, pid);
    }

    public long getPid(){
        return roleDO.getPid();
    }
}
