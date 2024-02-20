package com.ares.login.discovery;

import com.ares.discovery.DiscoveryService;
import com.ares.discovery.transfer.OnWatchServiceChange;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.client.AresTcpClient;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
@Slf4j
public class OnDiscoveryWatch implements OnWatchServiceChange {
    private static final  String  ONLINE_COUNT ="OC";
    @Autowired
    private DiscoveryService discoveryService;

    private final Map<Integer,ConcurrentSkipListSet<ServerNodeInfo>> areaGateways = new ConcurrentHashMap<>();


    public ServerNodeInfo  getLowerLoadServer(int areaId){
        ConcurrentSkipListSet<ServerNodeInfo> serverNodeInfos = areaGateways.get(areaId);
        if(CollectionUtils.isEmpty(serverNodeInfos)){
            return  null;
        }
        return serverNodeInfos.first();
    }

    @Override
    public Void onWatchServiceChange(WatchEvent.EventType eventType, ServerNodeInfo serverNodeInfo) {
        log.info("+++++++ watcher server info ={}  type={}",serverNodeInfo, eventType);
        if(eventType == WatchEvent.EventType.PUT){
            ConcurrentSkipListSet<ServerNodeInfo> serverNodeInfos = areaGateways.computeIfAbsent(serverNodeInfo.getAreaId(), (value) -> createGatewayWatcher());
            serverNodeInfos.add(serverNodeInfo);
            return null;
        }

        if(eventType == WatchEvent.EventType.DELETE){
            ConcurrentSkipListSet<ServerNodeInfo> serverNodeInfos = areaGateways.get(serverNodeInfo.getAreaId());
            if(serverNodeInfos == null){
                return null;
            }
            serverNodeInfos.remove(serverNodeInfo);
            return null;
        }
        log.warn("XXXXXXXXXXXXx serverInfo ={} type ={}  not process", serverNodeInfo, eventType);
        return null;
    }

    private  ConcurrentSkipListSet<ServerNodeInfo>   createGatewayWatcher()  {
        return new ConcurrentSkipListSet<>((o1, o2) -> {
            String strO1Count = o1.getMetaData().get(ONLINE_COUNT);
            int o1Count = 0;
            if( strO1Count != null){
                o1Count = Integer.parseInt(strO1Count);
            }

            String strO2Count = o2.getMetaData().get(ONLINE_COUNT);
            int o2Count = 0;
            if( strO2Count != null){
                o2Count = Integer.parseInt(strO2Count);
            }
            return   o1Count - o2Count;
        });
    }
}
