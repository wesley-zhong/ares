package com.ares.team.configuration;

import com.ares.core.tcp.AresTcpHandler;
import com.ares.team.network.WorldMsgHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.ares")
public class WorldConfiguration {
    @Bean
    public AresTcpHandler aresTcpHandler() {
        return new WorldMsgHandler();
    }
}
