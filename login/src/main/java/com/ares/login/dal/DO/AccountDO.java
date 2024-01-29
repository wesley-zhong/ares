package com.ares.login.dal.DO;

import com.ares.dal.DO.CASDO;
import com.ares.dal.mongo.MongoBaseDAO;

public class AccountDO extends CASDO {
    private String accountId;
    private long roleId;
    private String channel;
}
