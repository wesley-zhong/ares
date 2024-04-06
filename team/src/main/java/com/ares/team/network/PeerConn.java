package com.ares.team.network;


import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.transport.peer.PeerConnBase;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class PeerConn extends PeerConnBase {
    private final Map<Long, ChannelHandlerContext> playerIdContext = new ConcurrentHashMap<>();

    public void sendToGameMsg(long roleId, int msgId, Message body) {
        send(ServerType.GAME, roleId, msgId, body);
    }

    public void redirectToGameMsg(long roleId, AresPacket aresPacket) {
        innerRedirectTo(ServerType.GAME, roleId, aresPacket);
    }

    @Override
    public ChannelHandlerContext loadBalance(int serverType, long roleId, Map<String, ChannelHandlerContext> channelConMap) {
        ChannelHandlerContext channelHandlerContext = playerIdContext.get(roleId);
        if(channelHandlerContext != null){
            return channelHandlerContext;
        }
        return null;
    }

    public void routerToTeam(long roleId, AresPacket aresPacket) {
//        AresTKcpContext aresTcpContext = getAresTcpContext(areaId, ServerType.ROUTER);
//        if (aresTcpContext == null) {
//            log.error("areaId ={} serverType ={}  not found connection", aresPacket, ServerType.ROUTER);
//            return;
//        }
//
//        aresPacket.getRecvByteBuf().readerIndex(0);
//        aresTcpContext.send(aresPacket.getRecvByteBuf().retain());

        innerRedirectTo(ServerType.ROUTER,roleId, aresPacket);
    }


    public void routerToOtherGame(long roleId, AresPacket aresPacket) {
//        AresTKcpContext aresTcpContext = getAresTcpContext(areaId, ServerType.ROUTER);
//        if (aresTcpContext == null) {
//            log.error("areaId ={} serverType ={}  not found connection", aresPacket, ServerType.ROUTER);
//            return;
//        }
//
//        aresPacket.getRecvByteBuf().readerIndex(0);
//        aresTcpContext.send(aresPacket.getRecvByteBuf().retain());

        innerRedirectTo(ServerType.GAME,roleId, aresPacket);
    }

    public void sendGateWayMsg( long roleId, int msgId, Message body) {
        send(ServerType.GAME,roleId, msgId, body);
    }
}
