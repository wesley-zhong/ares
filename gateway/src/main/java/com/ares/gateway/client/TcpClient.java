package com.ares.gateway.client;

import com.ares.gateway.configuration.GameServerInfoList;
import com.ares.transport.client.AresTcpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TcpClient  implements InitializingBean {
    private final Map<Integer, Channel> channelMap = new HashMap<>();

    @Autowired
    private GameServerInfoList gameServerInfoList;

    @Autowired
    private AresTcpClient  aresTcpClient;





    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("------ tcp client init success");
      //  Channel channel = aresTcpClient.connect("127.0.0.1", 8081);
    }
}
