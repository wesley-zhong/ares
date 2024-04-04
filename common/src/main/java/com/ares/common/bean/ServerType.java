package com.ares.common.bean;

import lombok.Getter;
import lombok.Setter;

public enum ServerType {
    LOGIN(0,"login"),
    GATEWAY(1, "gateway"),
    GAME(2, "game"),
    ROUTER(3, "router"),

    TEAM(4,"team");

    @Getter
    private final int value;
    @Getter
    private final String name;

    private ServerType(int serverType, String serverName) {
        this.value = serverType;
        this.name = serverName;
    }

    public  static  ServerType from(String serverName){
        for (ServerType value : ServerType.values()) {
            if(serverName.contains(value.name)){
                return value;
            }
        }
        return  null;
    }
}
