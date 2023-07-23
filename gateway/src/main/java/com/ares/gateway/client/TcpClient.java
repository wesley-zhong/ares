package com.ares.gateway.client;

import com.ares.transport.client.AresTcpClient;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TcpClient  implements InitializingBean {
    @Autowired
    private AresTcpClient  aresTcpClient;
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("------ tcp client init success");
      //  Channel channel = aresTcpClient.connect("127.0.0.1", 8081);
    }
}
