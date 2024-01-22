package com.ares.game.service;

import com.ares.game.DO.AccountDO;
import com.ares.game.dao.AccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerRoleService {
    @Autowired
    private AccountDAO accountDAO;

    public AccountDO getRoleDo(long id){
        return  accountDAO.getSingle(id);
    }
}
