package com.ares.world.player;

import com.ares.core.tcp.AresTKcpContext;
import com.ares.world.network.PeerTransfer;
import io.netty.channel.ChannelHandlerContext;

public class WorldPlayer   extends PeerTransfer {
    public WorldPlayer(AresTKcpContext context, long pid) {
        super(context, pid);
    }
}
