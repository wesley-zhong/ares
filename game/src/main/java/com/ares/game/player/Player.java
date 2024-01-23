package com.ares.game.player;

import com.ares.game.network.PeerTransfer;
import io.netty.channel.ChannelHandlerContext;

public class Player  extends PeerTransfer {
    private long pid;

    public Player(ChannelHandlerContext context) {
        super(context);
        this.pid = pid;
    }
}
