package com.ares.team.service;

import com.ares.core.tcp.AresTKcpContext;
import com.ares.team.player.WorldPlayer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorldPlayerMgr {
    private final Map<Long, WorldPlayer> playerMap = new HashMap<>();

    public WorldPlayer getPlayer(long pid) {
        return playerMap.get(pid);
    }

    public WorldPlayer crateWorldPlayer(AresTKcpContext context, long pid) {
        return new WorldPlayer(context, pid);
    }
}
