package com.ares.world.network;


import com.ares.core.tcp.AresTKcpContext;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;


@Component
public class PeerConn {
    private final Map<Integer, Map<String, ChannelHandlerContext>> peerConns = new HashMap<>();
    public synchronized void  addContext(int areaId, String serviceName, AresTKcpContext aresTKcpContext){
        Map<String, ChannelHandlerContext> stringAresTcpContextMap = peerConns.get(areaId);
        if(stringAresTcpContextMap == null){
            stringAresTcpContextMap = new HashMap<>();
            stringAresTcpContextMap.put(serviceName, aresTKcpContext.getCtx());
            peerConns.put(areaId, stringAresTcpContextMap );
            return;
        }
        stringAresTcpContextMap.put(serviceName, aresTKcpContext.getCtx());
    }

    public ChannelHandlerContext getAresTcpContext(int areaId, String serviceName){
        Map<String, ChannelHandlerContext> stringAresTcpContextMap = peerConns.get(areaId);
        if(CollectionUtils.isEmpty(stringAresTcpContextMap)){
            return null;
        }
        return stringAresTcpContextMap.get(serviceName);
    }
}
