package com.ares.gateway.configuration;

import com.ares.core.tcp.AresTcpHandler;
import com.ares.transport.client.AresTcpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.ares")
public class GatewayConfiguration {
    @Bean
    public AresTcpClient aresTcpClient(@Autowired  AresTcpHandler  aresTcpHandler){
        AresTcpClient aresTcpClient = new AresTcpClient();
        aresTcpClient.init(aresTcpHandler);
        return aresTcpClient;
    }

}
