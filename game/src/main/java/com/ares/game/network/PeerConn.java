package com.ares.game.network;


import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.game.player.GamePlayer;
import com.game.protoGen.ProtoInner;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class PeerConn {
    @Value("${area.id}")
    private int areaId;
    private final Map<Integer, Map<Integer, AresTKcpContext>> peerConns = new HashMap<>();

    public synchronized void addContext(int areaId, String serviceName, AresTKcpContext aresTKcpContext) {
        ServerType serverType = ServerType.from(serviceName);
        if (serverType == null) {
            log.error("service name == {} not be defined in ServerType enum", serviceName);
            return;
        }
        Map<Integer, AresTKcpContext> stringAresTcpContextMap = peerConns.get(areaId);
        if (stringAresTcpContextMap == null) {
            stringAresTcpContextMap = new HashMap<>();
            stringAresTcpContextMap.put(serverType.getValue(), aresTKcpContext);
            peerConns.put(areaId, stringAresTcpContextMap);
            return;
        }
        stringAresTcpContextMap.put(serverType.getValue(), aresTKcpContext);
    }


    public synchronized AresTKcpContext getAresTcpContext(ServerType serverType) {
        return getAresTcpContext(areaId, serverType);
    }

    public synchronized AresTKcpContext getAresTcpContext(int areaId, ServerType serverType) {
        Map<Integer, AresTKcpContext> stringAresTcpContextMap = peerConns.get(areaId);
        if (CollectionUtils.isEmpty(stringAresTcpContextMap)) {
            return null;
        }
        return stringAresTcpContextMap.get(serverType.getValue());
    }

    public void send(int areaId, ServerType serverType, long roleId, int msgId, Message body) {
        AresTKcpContext channelHandlerContext = getAresTcpContext(areaId, serverType);
        if (channelHandlerContext == null) {
            log.error("areaId ={} sererType ={}  not found to send msgId ={}", areaId, serverType, msgId);
            return;
        }
        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().setRoleId(roleId).build();
        AresPacket aresPacket = AresPacket.create(msgId, header, body);
        channelHandlerContext.send(aresPacket);
    }

    public void send(GamePlayer gamePlayer, int msgId, Message body) {
        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().setRoleId(gamePlayer.getPid()).build();
        AresPacket aresPacket = AresPacket.create(msgId, header, body);
        gamePlayer.sendToGateway(aresPacket);
    }


    public void send(ServerType serverType, long roleId, int msgId, Message body) {
        send(areaId, serverType, roleId, msgId, body);
    }

    public void sendWorldMsg(int areaId, long roleId, int msgId, Message body) {
        send(areaId, ServerType.WORLD, roleId, msgId, body);
    }

    public void sendWorldMsg(long roleId, int msgId, Message body) {
        send(this.areaId, ServerType.WORLD, roleId, msgId, body);
    }

    public void sendGateWayMsg(GamePlayer gamePlayer, long roleId, int msgId, Message body) {
        send(gamePlayer, msgId, body);
    }

    public void sendGateWayMsg(GamePlayer gamePlayer, int msgId, Message body) {
        send(gamePlayer, msgId, body);
    }

    public void directSendToWorld(AresPacket aresPacket) {
        AresTKcpContext aresTcpContext = getAresTcpContext(areaId, ServerType.WORLD);
        if (aresTcpContext == null) {
            log.error("areaId ={} serverType ={}  not found connection", aresPacket, ServerType.WORLD);
            return;
        }

        aresPacket.getRecvByteBuf().readerIndex(0);
        aresTcpContext.send(aresPacket.getRecvByteBuf().retain());
    }

    public void directSendToGateway(long pid,AresPacket aresPacket) {
        AresTKcpContext aresTcpContext = getAresTcpContext(areaId, ServerType.GATEWAY);
        if (aresTcpContext == null) {
            log.error("areaId ={} serverType ={}  not found connection", aresPacket, ServerType.WORLD);
            return;
        }
        aresTcpContext.send(aresPacket.getRecvByteBuf().retain());
    }
}
