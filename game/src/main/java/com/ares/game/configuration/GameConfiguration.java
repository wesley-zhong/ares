package com.ares.game.configuration;

import com.ares.common.bean.ServerType;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.game.network.GameMsgHandler;
import com.ares.core.utils.SnowFlake;
import com.ares.dal.mongo.AresMongoClient;
import com.ares.transport.client.AresTcpClient;
import com.ares.transport.client.AresTcpClientConn;
import com.ares.transport.client.AresTcpClientImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@ComponentScan("com.ares")
public class GameConfiguration  implements InitializingBean {
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


    @Bean
    @Lazy
    public AresTcpClient aresTcpClient(@Autowired @Lazy AresTcpClientConn conn) {
        AresTcpClient aresTcpClient = new AresTcpClientImpl(conn);
        aresTcpClient.init();
        return aresTcpClient;
    }

    @Bean
    public AresMongoClient aresMongoClient(@Autowired MongoConfig mongoConfig) {
        AresMongoClient mongoClient = new AresMongoClient(mongoConfig.getAddrs(), mongoConfig.getUserName(), mongoConfig.getPassword());
        mongoClient.init();
        return mongoClient;
    }

    @Bean
    public AresTcpHandler aresTcpHandler() {
        return new GameMsgHandler();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SnowFlake.init(areaId, ServerType.GAME.getValue());
    }
}
