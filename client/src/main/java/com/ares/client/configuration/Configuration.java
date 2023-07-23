package com.ares.client.configuration;

import com.ares.client.network.TcpNetWorkHandlerImpl;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.tcp.TcpRequestTcpHandler;
import com.ares.transport.client.AresTcpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@org.springframework.context.annotation.Configuration
@ComponentScan({"com.ares.core","com.ares.client"})
public class Configuration {

    @Bean
    public AresTcpClient aresTcpClient(@Autowired  AresTcpHandler  aresTcpHandler){
        AresTcpClient aresTcpClient = new AresTcpClient();
        aresTcpClient.init(aresTcpHandler);
        return aresTcpClient;
    }

    @Bean
    public AresTcpHandler aresTcpHandler(){
        return new TcpRequestTcpHandler();
    }

}
