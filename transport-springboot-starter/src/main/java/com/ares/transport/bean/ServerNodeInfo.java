package com.ares.transport.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ServerNodeInfo {
    private String serviceId;
    private String serviceName;
    private String ip;
    private int port;
    private int areaId;
    private Map<String, Object> metaData = new HashMap<>();
}
