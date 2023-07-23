package com.ares.transport.surpport;


import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.tcp.TcpRequestTcpHandler;
import com.ares.transport.broker.AresNettyServer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.config.client.ConfigServiceBootstrapConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnClass(ConfigServiceBootstrapConfiguration.class)
public class NplServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(AresRpcServerConfiguration.AresRpcServer.class)
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public AresNettyServer aresNettyServer() {
        return new AresNettyServer();
    }

    @Bean
    @ConditionalOnMissingBean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public AresTcpHandler aresTcpHandler() {
        return new TcpRequestTcpHandler();
    }
}
