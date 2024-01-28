package com.ares.login.configuration;

import com.ares.common.bean.ServerType;

import com.ares.core.utils.SnowFlake;
import com.ares.dal.mongo.AresMongoClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("com.ares")
public class GameConfiguration  implements InitializingBean {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private int serverPort;


    @Bean
    public AresMongoClient aresMongoClient(@Autowired MongoConfig mongoConfig) {
        AresMongoClient mongoClient = new AresMongoClient(mongoConfig.getAddrs(), mongoConfig.getUserName(), mongoConfig.getPassword());
        mongoClient.init();
        return mongoClient;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        /**
         * this should increased from etcd
         */
        SnowFlake.init(0, ServerType.GAME.getValue());
    }
}
