package com.ares.game.configuration;

import com.ares.common.bean.ServerInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "world-server")
@Getter
@Setter
public class WorldServerInfoList {
    private List<ServerInfo> servers;
}