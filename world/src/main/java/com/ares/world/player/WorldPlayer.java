package com.ares.world.player;

import com.ares.world.network.PeerTransfer;
import io.netty.channel.ChannelHandlerContext;

public class WorldPlayer   extends PeerTransfer {
    public WorldPlayer(ChannelHandlerContext context, long pid) {
        super(context);
    }
}
