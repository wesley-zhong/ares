package com.ares.team.network;


import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
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
    @Value("${area.id:100}")
    private int areaId;
    private final Map<Integer, Map<Integer, ChannelHandlerContext>> peerConns = new HashMap<>();

    public synchronized void addContext(int areaId, String serviceName, AresTKcpContext aresTKcpContext) {
        ServerType serverType = ServerType.from(serviceName);
        if (serverType == null) {
            log.error("service name == {} not be defined in ServerType enum", serviceName);
            return;
        }
        Map<Integer, ChannelHandlerContext> stringAresTcpContextMap = peerConns.get(areaId);
        if (stringAresTcpContextMap == null) {
            stringAresTcpContextMap = new HashMap<>();
            stringAresTcpContextMap.put(serverType.getValue(), aresTKcpContext.getCtx());
            peerConns.put(areaId, stringAresTcpContextMap);
            return;
        }
        stringAresTcpContextMap.put(serverType.getValue(), aresTKcpContext.getCtx());
    }

    public ChannelHandlerContext getAresTcpContext(ServerType serverType) {
        return getAresTcpContext(areaId, serverType);
    }

    public ChannelHandlerContext getAresTcpContext(int areaId, ServerType serverType) {
        Map<Integer, ChannelHandlerContext> stringAresTcpContextMap = peerConns.get(areaId);
        if (CollectionUtils.isEmpty(stringAresTcpContextMap)) {
            return null;
        }
        return stringAresTcpContextMap.get(serverType.getValue());
    }

    public void send(int areaId, ServerType serverType, long roleId, int msgId, Message body) {
        ChannelHandlerContext aresTcpContext = getAresTcpContext(areaId, serverType);
        if (aresTcpContext == null) {
            log.error("========= areaId = {} servetType={} not connected", areaId, serverType);
            return;
        }
        ProtoInner.InnerMsgHeader innerMsgHeader = ProtoInner.InnerMsgHeader.newBuilder().setRoleId(roleId).build();
        AresPacket aresPacket = AresPacket.create(msgId, innerMsgHeader, body);
        aresTcpContext.writeAndFlush(aresPacket);
    }

    public void sendToGame(int areaId, long roleId, int msgId, Message body) {
        send(areaId, ServerType.GAME, roleId,msgId, body);
    }
    public void sendToGame(long roleId, int msgId, Message body){
        send(this.areaId, ServerType.GAME, roleId, msgId,body);
    }
}
