package com.ares.transport.surpport;

import com.ares.transport.server.AresNettyServer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

//@Configuration
public class AresTcpServerConfiguration {

//    @Bean
//    public AresTcpServer aresRpcServer() {
//        return new AresTcpServer();
//    }

    @Bean
    @ConditionalOnMissingBean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public AresNettyServer aresNettyServer() {
        return new AresNettyServer();
    }
//    public static class AresTcpServer {
//
//    }
}
