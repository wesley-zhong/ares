package com.ares.transport.surpport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AresTcpServerConfiguration {

    @Bean
    public AresTcpServer aresRpcServer() {
        return new AresTcpServer();
    }


    public static class AresTcpServer {

    }
}
