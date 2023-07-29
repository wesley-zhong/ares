package com.ares.client;

import com.ares.transport.client.AresTcpClient;
import io.netty.channel.Channel;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Client  implements InitializingBean {
    @Autowired
    private AresTcpClient aresTcpClient ;
    @Getter
    private Channel channel;
    @Autowired
    private  LoginService  loginService;
    @Override
    public void afterPropertiesSet() throws Exception {
       channel= aresTcpClient.connect("127.0.0.1", 8081);
       loginService.loginRequest(channel);
    }
}
