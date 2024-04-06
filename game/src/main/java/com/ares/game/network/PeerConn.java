package com.ares.game.network;


import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.game.discovery.OnDiscoveryWatchService;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.peer.PeerConnBase;
import com.google.protobuf.Message;
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
    private final Map<Long, GamePlayerInterTransferInfo> playerIdContext = new ConcurrentHashMap<>();

    public void recordPlayerFromContext(ServerType serverType, long playerId, ChannelHandlerContext channelHandlerContext) {
        GamePlayerInterTransferInfo gamePlayerInterTransferInfo = playerIdContext.get(playerId);
        if (gamePlayerInterTransferInfo == null) {
            gamePlayerInterTransferInfo = new GamePlayerInterTransferInfo();
            gamePlayerInterTransferInfo.setContext(serverType.getValue(), channelHandlerContext);
            playerIdContext.put(playerId, gamePlayerInterTransferInfo);
        }
        gamePlayerInterTransferInfo.setContext(serverType.getValue(), channelHandlerContext);
    }

    @Override
    public ChannelHandlerContext loadBalance(int serverType, long roleId, Map<String, ChannelHandlerContext> channelConMap) {
        GamePlayerInterTransferInfo channelHandlerContext = playerIdContext.get(roleId);
        if (channelHandlerContext != null) {
            ChannelHandlerContext contextByType = channelHandlerContext.getContextByType(serverType);
            if (contextByType != null) {
                return contextByType;
            }
        }
        // channelHandlerContext it should be first create when player login
        // if not exist only one case
        if(channelHandlerContext == null){
            log.warn(" server type={} roleId = {} something error", serverType, roleId);
            return  null;
        }
        ServerNodeInfo lowerLoadServerNodeInfo = onDiscoveryWatchService.getLowerLoadServerNodeInfo(serverType);
        ChannelHandlerContext context = getServerConnByServerInfo(lowerLoadServerNodeInfo);
        if (context == null) {
            return null;
        }
        channelHandlerContext.setContext(serverType, context);
        return context;
    }

    //router proxy the msg to team
    public void routerToTeam(long roleId, AresPacket aresPacket) {
        innerRedirectTo(ServerType.ROUTER, roleId, aresPacket);
    }


    public void routerToOtherGame(long roleId, AresPacket aresPacket) {
        innerRedirectTo(ServerType.GAME, roleId, aresPacket);
    }

    public void sendGateWayMsg(long roleId, int msgId, Message body) {
        send(ServerType.GATEWAY, roleId, msgId, body);
    }

    public void directToGateway(long pid, AresPacket aresPacket) {
        innerRedirectTo(ServerType.GATEWAY, pid, aresPacket);
    }

}
