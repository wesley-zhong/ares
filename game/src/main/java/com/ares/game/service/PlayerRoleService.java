package com.ares.game.service;

import com.ares.game.DO.RoleDO;
import com.ares.game.dao.RoleDAO;
import com.ares.game.player.GamePlayer;
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
            RoleDO roleDO = roleDAO.getById(pid);
            if(roleDO == null){
                return  null;
            }
            gamePlayer = new GamePlayer(pid);
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
        GamePlayer gamePlayer = new GamePlayer(roleId);
        RoleDO roleDO = new RoleDO();
        roleDO.setPid(roleId);
        roleDO.setId(roleId);
        roleDO.setName(name);
        roleDAO.insert(roleDO);
        gamePlayer.setRoleDO(roleDO);
        playerMap.put(roleId, gamePlayer);
        return gamePlayer;
    }

    public void asynUpdateTest(RoleDO roleDO){
        for(int i = 0;  i < 2000; i++){
            roleDO.setCountTest(roleDO.getCountTest()  + 1);
            roleDAO.asynUpInsert(roleDO);
        }
    }

}
