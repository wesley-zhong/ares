package com.ares.gateway.configuration;


import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//@Component
//@ConfigurationProperties(prefix = "discovery")
//@Getter
//@Setter
public class DiscoveryEndPoints {
    private String[] endpoints;
    private WatchInfo[] watchServers;


    @Getter
    @Setter
    public static class WatchInfo {
        private String serviceNamePrefix;
        private Areas[] areas;

        public List<String> getWatchPrefix() {
            List<String> watchList = new ArrayList<>();
            for (Areas area : areas) {
                watchList.add(area.areaId + "/" + serviceNamePrefix);
            }
            return watchList;
        }
    }

    @Getter
    @Setter
    public static class Areas {
        private int areaId;
        private boolean connected;
    }
}
