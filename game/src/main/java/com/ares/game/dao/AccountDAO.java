package com.ares.game.dao;

import com.ares.dal.mongo.MongoBaseDAO;
import com.ares.dal.mongo.annotation.MdbName;
import com.ares.game.DO.AccountDO;
import org.springframework.stereotype.Repository;

@Repository

public class AccountDAO extends MongoBaseDAO<AccountDO> {
    public AccountDAO() {
        super(AccountDO.class);
    }
}
