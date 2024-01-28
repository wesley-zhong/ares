package com.ares.world.configuration;

import com.ares.core.tcp.AresTcpHandler;
import com.ares.transport.client.AresTcpClient;
import com.ares.transport.client.AresTcpClientConn;
import com.ares.transport.client.AresTcpClientImpl;
import com.ares.world.network.WorldMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@ComponentScan("com.ares")
public class WorldConfiguration {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private int serverPort;

    @Value("${area.id:100}")
    private int areaId;

    @Bean
    public AresTcpClientConn aresTcpClientConn(@Autowired AresTcpHandler aresTcpHandler) {
        AresTcpClientConn aresTcpClientConn = new AresTcpClientConn();
        aresTcpClientConn.init(aresTcpHandler);
        return aresTcpClientConn;
    }

    //    @Bean
//    @Lazy
//    public AresTcpClient  aresTcpClient(@Autowired WorldServerInfoList serverInfoList, @Autowired @Lazy AresTcpClientConn conn){
//        AresTcpClient aresTcpClient = new AresTcpClientImpl(serverInfoList.getServers(), conn);
//        aresTcpClient.init();
//        return aresTcpClient;
//    }
//
//
    @Bean
    @Lazy
    public AresTcpClient aresTcpClient(@Autowired @Lazy AresTcpClientConn conn) {
        AresTcpClient aresTcpClient = new AresTcpClientImpl(conn);
        aresTcpClient.init();
        return aresTcpClient;
    }
    @Bean
    public AresTcpHandler aresTcpHandler() {
        return new WorldMsgHandler();
    }
}
