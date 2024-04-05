package com.ares.team.discovery;

import com.ares.discovery.DiscoveryService;
import com.ares.discovery.transfer.OnWatchServiceChange;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.client.AresTcpClient;
import io.etcd.jetcd.watch.WatchEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OnDiscoveryWatch implements OnWatchServiceChange {
    @Autowired
    private AresTcpClient aresTcpClient;
    @Autowired
    private DiscoveryService discoveryService;

    @Override
    public Void onWatchServiceChange(WatchEvent.EventType eventType, ServerNodeInfo serverNodeInfo) {
        if(eventType == WatchEvent.EventType.PUT) {
            aresTcpClient.connect(serverNodeInfo);
            return  null;
        }
        if(eventType == WatchEvent.EventType.DELETE){
            aresTcpClient.close(serverNodeInfo);
        }
        return null;
    }
}
