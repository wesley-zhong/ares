package com.ares.game.player;

import com.ares.game.DO.RoleDO;
import com.ares.game.network.PeerTransfer;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GamePlayer extends PeerTransfer {
    private RoleDO roleDO;

    public GamePlayer(){
    }
    public GamePlayer(ChannelHandlerContext context) {
        super(context);
    }

    public long getPid(){
        return roleDO.getPid();
    }
}
