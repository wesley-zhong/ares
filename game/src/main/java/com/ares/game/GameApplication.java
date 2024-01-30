package com.ares.game;

import com.ares.discovery.annotation.EnableAresDiscovery;
import com.ares.transport.annotation.EnableAresTcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAresDiscovery("com.ares.game.network")
@EnableAresTcpServer
public class GameApplication {
    public static void main(String[] args) {
       SpringApplication.run(GameApplication.class, args);
    }
}
