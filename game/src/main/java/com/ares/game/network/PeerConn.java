package com.ares.game.network;


import com.ares.core.tcp.AresTKcpContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;


@Component
public class PeerConn {
    private final Map<Integer, Map<String, AresTKcpContext>> peerConns = new HashMap<>();
    public synchronized void  addContext(int areaId, String serviceName, AresTKcpContext aresTKcpContext){
        Map<String, AresTKcpContext> stringAresTcpContextMap = peerConns.get(areaId);
        if(stringAresTcpContextMap == null){
            stringAresTcpContextMap = new HashMap<>();
            stringAresTcpContextMap.put(serviceName, aresTKcpContext);
            peerConns.put(areaId, stringAresTcpContextMap );
            return;
        }
        stringAresTcpContextMap.put(serviceName, aresTKcpContext);
    }

    public AresTKcpContext getAresTcpContext(int areaId, String serviceName){
        Map<String, AresTKcpContext> stringAresTcpContextMap = peerConns.get(areaId);
        if(CollectionUtils.isEmpty(stringAresTcpContextMap)){
            return null;
        }
        return stringAresTcpContextMap.get(serviceName);
    }
}
