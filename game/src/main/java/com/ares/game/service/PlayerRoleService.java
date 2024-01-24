package com.ares.game.service;

import com.ares.core.utils.SnowFlake;
import com.ares.game.DO.RoleDO;
import com.ares.game.dao.PlayerDAO;
import com.ares.game.player.GamePlayer;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PlayerRoleService {
    @Autowired
    private PlayerDAO playerDAO;

    private final Map<Long, GamePlayer> playerMap = new HashMap<>();

    public GamePlayer getPlayer(long pid) {
        return playerMap.get(pid);
    }

    public GamePlayer getRoleDo(long id) {
        return playerMap.get(id);
    }

    public GamePlayer createGamePlayer(ChannelHandlerContext channelHandlerContext, String name) {
        long pid = SnowFlake.nextId();
        GamePlayer gamePlayer = new GamePlayer(channelHandlerContext, pid);
        RoleDO roleDO = new RoleDO();
        roleDO.setPid(pid);
        roleDO.setName(name);
        playerDAO.insert(roleDO);
        gamePlayer.setRoleDO(roleDO);
        return gamePlayer;
    }
}
