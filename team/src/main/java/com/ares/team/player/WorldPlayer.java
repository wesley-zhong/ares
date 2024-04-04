package com.ares.team.player;

import com.ares.core.tcp.AresTKcpContext;
import com.ares.team.network.PeerTransfer;

public class WorldPlayer   extends PeerTransfer {
    public WorldPlayer(AresTKcpContext context, long pid) {
        super(context, pid);
    }
}
