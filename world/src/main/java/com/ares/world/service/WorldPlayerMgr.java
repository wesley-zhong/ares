package com.ares.world.service;

import com.ares.world.player.WorldPlayer;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorldPlayerMgr {
    private final Map<Long, WorldPlayer> playerMap = new HashMap<>();

    public WorldPlayer getPlayer(long pid) {
        return playerMap.get(pid);
    }

    public WorldPlayer crateWorldPlayer(ChannelHandlerContext context, long pid) {
        return new WorldPlayer(context, pid);
    }
}
