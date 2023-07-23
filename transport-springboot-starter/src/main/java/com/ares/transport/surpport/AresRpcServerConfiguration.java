package com.ares.transport.surpport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AresRpcServerConfiguration {

    @Bean
    public AresRpcServer aresRpcServer() {
        return new AresRpcServer();
    }


    public static class AresRpcServer {

    }
}
