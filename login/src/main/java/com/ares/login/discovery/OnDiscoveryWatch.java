package com.ares.login.discovery;

import com.ares.discovery.DiscoveryService;
import com.ares.discovery.transfer.OnWatchServiceChange;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.client.AresTcpClient;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OnDiscoveryWatch implements OnWatchServiceChange {
    @Autowired
    private DiscoveryService discoveryService;

    @Override
    public Void onWatchServiceChange(WatchEvent.EventType eventType, ServerNodeInfo serverNodeInfo) {
        log.info("+++++++ watcher server info ={}  type={}",serverNodeInfo, eventType);
        return null;
    }
}
