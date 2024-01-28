package com.ares.game.service;

import com.ares.game.DO.RoleDO;
import com.ares.game.dao.RoleDAO;
import com.ares.game.player.GamePlayer;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PlayerRoleService {
    @Autowired
    private RoleDAO roleDAO;

    private final Map<Long, GamePlayer> playerMap = new HashMap<>();

    public GamePlayer getPlayer(long pid) {
        GamePlayer gamePlayer =  playerMap.get(pid);
        if(gamePlayer == null){
            RoleDO roleDO = roleDAO.getSingle(pid);
            if(roleDO == null){
                return  null;
            }
            gamePlayer = new GamePlayer();
            gamePlayer.setRoleDO(roleDO);
        }
         playerMap.put(pid, gamePlayer);
        return gamePlayer;
    }

    public GamePlayer getRoleDo(long id) {
        return playerMap.get(id);
    }

    public GamePlayer createGamePlayer(long roleId, String name) {
        //long pid = SnowFlake.nextId();
        GamePlayer gamePlayer = new GamePlayer();
        RoleDO roleDO = new RoleDO();
        roleDO.setPid(roleId);
        roleDO.setId(roleId);
        roleDO.setName(name);
        roleDAO.insert(roleDO);
        gamePlayer.setRoleDO(roleDO);
        playerMap.put(roleId, gamePlayer);
        return gamePlayer;
    }

}
