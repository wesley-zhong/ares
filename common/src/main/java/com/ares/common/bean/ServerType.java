package com.ares.common.bean;

import lombok.Getter;
import lombok.Setter;

public enum ServerType {
    GATEWAY(1, "gateway"),
    GAME(2, "game"),

    WORLD(3,"world");

    @Getter
    private int value;
    @Getter
    private String name;

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
