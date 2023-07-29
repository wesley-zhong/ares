package com.ares.world;


import com.ares.transport.annotation.EnableAresTcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAresTcpServer
@ComponentScan("com.ares")
public class WorldApplication
{
    public static void main(String[] args) {
        SpringApplication.run(WorldApplication.class, args);
    }
}
