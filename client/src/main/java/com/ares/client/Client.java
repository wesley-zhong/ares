package com.ares.client;

import com.ares.transport.client.AresTcpClientConn;
import io.netty.channel.Channel;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Client implements InitializingBean {
    @Autowired
    private AresTcpClientConn aresTcpClientConn;
    @Getter
    private Channel channel;
    @Autowired
    private LoginService loginService;
    private final static int PLAYER_COUNT = 2;

    @Override
    public void afterPropertiesSet() throws Exception {
        long roleId = System.currentTimeMillis();

        for (int i = 0; i < PLAYER_COUNT; ++i) {
            channel = aresTcpClientConn.connect("127.0.0.1", 6080);
            loginService.loginRequest(channel, roleId + i);
        }

    }
}
