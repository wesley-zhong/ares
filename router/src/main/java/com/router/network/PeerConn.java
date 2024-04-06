package com.router.network;


import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.peer.PeerConnBase;
import com.router.bean.RouterPlayerInterTransferInfo;
import com.router.discovery.OnDiscoveryWatchService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class PeerConn extends PeerConnBase {
    @Autowired
    private OnDiscoveryWatchService onDiscoveryWatchService;
    private final Map<Long, RouterPlayerInterTransferInfo> routerPlayerInterTransferInfoMap = new ConcurrentHashMap<>();


    public void routerToTeam(ChannelHandlerContext fromContext, int fromServetType, long roleId, AresPacket aresPacket) {
        RouterPlayerInterTransferInfo routerPlayerInterTransferInfo = routerPlayerInterTransferInfoMap.get(roleId);
        if (routerPlayerInterTransferInfo == null) {
            routerPlayerInterTransferInfo = new RouterPlayerInterTransferInfo();
            routerPlayerInterTransferInfo.setContext(fromServetType, fromContext);
            routerPlayerInterTransferInfoMap.put(roleId, routerPlayerInterTransferInfo);
        }

        innerRedirectTo(ServerType.ROUTER, roleId, aresPacket);
    }


    public void routerToGame(ChannelHandlerContext fromContext, int fromServetType, long roleId,long toRoleId,  AresPacket aresPacket) {
        //发送玩家第一次要记录从哪里来 一旦记下了后面都是通过这个 context 来发
        RouterPlayerInterTransferInfo routerPlayerInterTransferInfo = routerPlayerInterTransferInfoMap.get(roleId);
        if (routerPlayerInterTransferInfo == null) {
            routerPlayerInterTransferInfo = new RouterPlayerInterTransferInfo();
            routerPlayerInterTransferInfo.setContext(fromServetType, fromContext);
            routerPlayerInterTransferInfoMap.put(roleId, routerPlayerInterTransferInfo);
        }

        innerRedirectTo(ServerType.GAME, roleId, aresPacket);
    }


    @Override
    public ChannelHandlerContext loadBalance(int serverType, long roleId, Map<String, ChannelHandlerContext> channelConMap) {
        RouterPlayerInterTransferInfo routerPlayerInterTransferInfo = routerPlayerInterTransferInfoMap.get(roleId);
        if (routerPlayerInterTransferInfo == null) {
            log.error("roleId ={} serverType ={} not found ", roleId, serverType);
            return null;
        }
        ChannelHandlerContext contextByType = routerPlayerInterTransferInfo.getContextByType(serverType);
        if (contextByType == null) {
            ServerNodeInfo lowerLoadServerNodeInfo = onDiscoveryWatchService.getLowerLoadServerNodeInfo(serverType);
            ChannelHandlerContext serverConnByServerInfo = getServerConnByServerInfo(lowerLoadServerNodeInfo);
            routerPlayerInterTransferInfo.setContext(serverType, serverConnByServerInfo);
            return serverConnByServerInfo;
        }
        return contextByType;
    }
}
