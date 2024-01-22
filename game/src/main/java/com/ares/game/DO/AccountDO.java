package com.ares.game.DO;

import com.ares.dal.DO.CASDO;
import com.ares.dal.mongo.annotation.MdbName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MdbName("game")
public class AccountDO extends CASDO {
    private String account;
    private long pid;

}
