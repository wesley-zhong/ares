package com.ares.team.player;

import com.ares.core.tcp.AresTKcpContext;
import com.ares.team.network.PeerTransfer;

public class TeamPlayer extends PeerTransfer {
    public TeamPlayer(AresTKcpContext context, long pid) {
        super(context, pid);
    }
}
