package com.ares.game.configuration;

import com.ares.common.bean.ServerType;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.game.network.GameMsgHandler;
import com.ares.core.utils.SnowFlake;
import com.ares.dal.mongo.AresMongoClient;
import com.ares.transport.client.AresTcpClient;
import com.ares.transport.client.AresTcpClientConn;
import com.ares.transport.client.AresTcpClientImpl;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
@ComponentScan("com.ares")
public class GameConfiguration implements InitializingBean {
    private int areaId;

    @Bean
    public AresTcpClientConn aresTcpClientConn(@Autowired AresTcpHandler aresTcpHandler) {
        AresTcpClientConn aresTcpClientConn = new AresTcpClientConn();
        aresTcpClientConn.init(aresTcpHandler);
        return aresTcpClientConn;
    }

    @Bean
    @Lazy
    public AresTcpClient aresTcpClient(@Autowired @Lazy AresTcpClientConn conn) {
        AresTcpClient aresTcpClient = new AresTcpClientImpl(conn);
        aresTcpClient.init();
        return aresTcpClient;
    }

    @Bean
    public MongoClient mongoClient(@Autowired MongoConfig mongoConfig) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        fromProviders(PojoCodecProvider.builder().automatic(true).build());

        MongoCredential credential = MongoCredential.createCredential(mongoConfig.getUserName(), "admin", mongoConfig.getUserName().toCharArray());
        List<ServerAddress> mongoAddrsList = new ArrayList<>();
        String[] serverAddrs = mongoConfig.getAddrs().split(";");
        for (String serverAddr : serverAddrs) {
            ServerAddress serverAddress = new ServerAddress(serverAddr);
            mongoAddrsList.add(serverAddress);
        }
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyToClusterSettings(builder -> builder.hosts(mongoAddrsList))
                .codecRegistry(pojoCodecRegistry)
                .build();
        return MongoClients.create(settings);
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
