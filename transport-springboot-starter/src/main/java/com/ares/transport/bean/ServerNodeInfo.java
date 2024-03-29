package com.ares.transport.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ServerNodeInfo {
    private String serviceId;
    private String serviceName;
    private String ip;
    private boolean available;
    private int port;
    private int areaId;
    private Map<String, String> metaData = new HashMap<>();

    @Override
    public  int hashCode(){
        return serviceId.hashCode();
    }
    @Override
    public boolean  equals(Object o){
        if (this == o){
            return  true;
        }
        if (o.hashCode() != this.hashCode()){
            return false;
        }
        if(o instanceof  ServerNodeInfo  oS){
            return oS.getServiceId().equals(this.getServiceId());
        }
        return false;
    }
}
