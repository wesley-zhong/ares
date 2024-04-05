package com.ares.team.service;

import com.ares.core.tcp.AresTKcpContext;
import com.ares.team.player.TeamPlayer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TeamPlayerMgr {
    private final Map<Long, TeamPlayer> playerMap = new HashMap<>();

    public TeamPlayer getPlayer(long pid) {
        return playerMap.get(pid);
    }

    public TeamPlayer crateWorldPlayer(AresTKcpContext context, long pid) {
        return new TeamPlayer(context, pid);
    }
}
