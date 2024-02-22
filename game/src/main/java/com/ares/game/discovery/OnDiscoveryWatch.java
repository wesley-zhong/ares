package com.ares.game.discovery;

import com.ares.discovery.DiscoveryService;
import com.ares.discovery.transfer.OnWatchServiceChange;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.client.AresTcpClient;
import io.etcd.jetcd.watch.WatchEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class OnDiscoveryWatch implements OnWatchServiceChange, Ordered {
    @Autowired
    private AresTcpClient aresTcpClient;


    @Override
    public Void onWatchServiceChange(WatchEvent.EventType eventType, ServerNodeInfo serverNodeInfo) {
        aresTcpClient.connect(serverNodeInfo);
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
