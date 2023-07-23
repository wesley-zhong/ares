package com.ares.client;

import com.ares.transport.client.AresTcpClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Client  implements InitializingBean {
    @Autowired
    private AresTcpClient aresTcpClient ;
    @Override
    public void afterPropertiesSet() throws Exception {
        aresTcpClient.connect("127.0.0.1", 8081);
    }
}
