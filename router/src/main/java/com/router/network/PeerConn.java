package com.router.network;


import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.transport.peer.PeerConnBase;
import com.router.discovery.OnDiscoveryWatchService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;


@Component
@Slf4j
public class PeerConn extends PeerConnBase {
    @Autowired
    private OnDiscoveryWatchService onDiscoveryWatchService;
    //   private final Map<Long, RouterPlayerInterTransferInfo> routerPlayerInterTransferInfoMap = new ConcurrentHashMap<>();


    public void sendToTeam(long roleId, AresPacket aresPacket) {
        innerRedirectTo(ServerType.TEAM, roleId, aresPacket);
    }


    public void sendToGame(long roleId, AresPacket aresPacket) {
        innerRedirectTo(ServerType.GAME, roleId, aresPacket);
    }


    @Override
    public ChannelHandlerContext loadBalance(int serverType, long roleId, Map<String, ChannelHandlerContext> channelConMap) {
        if (CollectionUtils.isEmpty(channelConMap)) {
            log.error("loadBalance  roleId ={}  serverType={} not found ", roleId, serverType);
            return null;
        }
        return channelConMap.values().iterator().next();
    }
}
