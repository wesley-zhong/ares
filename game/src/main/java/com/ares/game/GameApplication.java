package com.ares.game;

import com.ares.transport.annotation.EnableAresTcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAresTcpServer
public class GameApplication {
    public static void main(String[] args) {
       SpringApplication.run(GameApplication.class, args);
       // new SpringApplicationBuilder(GatewayApplication.class).web(WebApplicationType.NONE).run(args);
    }
}
