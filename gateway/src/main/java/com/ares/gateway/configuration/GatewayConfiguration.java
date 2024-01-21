package com.ares.gateway.configuration;

import com.ares.discovery.DiscoveryService;
import com.ares.discovery.DiscoveryServiceImpl;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.transport.client.AresTcpClient;
import com.ares.transport.client.AresTcpClientConn;
import com.ares.transport.client.AresTcpClientImpl;
import io.etcd.jetcd.watch.WatchEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan("com.ares")
public class GatewayConfiguration {
    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private int serverPort;

    @Value("${area.id:100}")
    private int areaId;

    private AresTcpClient aresTcpClient;

    @Bean
    public AresTcpClientConn aresTcpClientConn(@Autowired AresTcpHandler aresTcpHandler) {
        AresTcpClientConn aresTcpClientConn = new AresTcpClientConn();
        aresTcpClientConn.init(aresTcpHandler);
        return aresTcpClientConn;
    }

//    @Bean
//    @Lazy
//    public AresTcpClient aresTcpClient(@Autowired GameServerInfoList serverInfoList, @Autowired @Lazy AresTcpClientConn conn) {
//        AresTcpClient aresTcpClient = new AresTcpClientImpl(serverInfoList.getServers(), conn);
//        aresTcpClient.init();
//        return aresTcpClient;
//    }

    @Bean
    @Lazy
    public AresTcpClient aresTcpClient(@Autowired @Lazy AresTcpClientConn conn) {
        aresTcpClient = new AresTcpClientImpl(conn);
        aresTcpClient.init();
        return aresTcpClient;
    }


    @Bean
    public DiscoveryService discoveryService(@Autowired DiscoveryEndPoints discoveryEndPoints) {
        DiscoveryServiceImpl etcdService = new DiscoveryServiceImpl();
        DiscoveryEndPoints.WatchInfo[] watchServers = discoveryEndPoints.getWatchServers();
        List<String> watchPreFixes = new ArrayList<>();
        for (DiscoveryEndPoints.WatchInfo watchInfo : watchServers) {
            List<String>watchList = watchInfo.getWatchPrefix();
            watchPreFixes.addAll(watchList);
        }
        etcdService.init(discoveryEndPoints.getEndpoints(), appName, serverPort,areaId, watchPreFixes, this::onWatchServiceChange);
        return etcdService;
    }

    public Void onWatchServiceChange(WatchEvent.EventType eventType, ServerNodeInfo serverNodeInfo) {
        aresTcpClient.connect(serverNodeInfo);
        return null;
    }
}
