package com.ares.transport.peer;

import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.transport.bean.ServerNodeInfo;
import com.game.protoGen.ProtoInner;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class PeerConnBase {
    /**
     * Integer key : server type
     * every server process has one connection, there may be many server processes with the same server type
     * String key : service_Id
     */
    private final Map<Integer, Map<String, ChannelHandlerContext>> serverTypeConnMap = new ConcurrentHashMap<>();


    public abstract ChannelHandlerContext loadBalance(int serverType, long roleId, Map<String, ChannelHandlerContext> channelConMap);

    public void send(ServerType serverType, long roleId, int msgId, Message body) {
        send(serverType.getValue(), roleId, msgId, body);
    }

    public void innerRedirectTo(ServerType serverType, long roleId, AresPacket aresPacket) {
        innerRedirectTo(serverType.getValue(), roleId, aresPacket);
    }


    public void addPeerConn(ServerNodeInfo serverNodeInfo, ChannelHandlerContext context) {
        addPeerConn(serverNodeInfo.getServerType(), serverNodeInfo.getServiceId(), context);
    }

    public void addPeerConn(int serverType, String serviceId, ChannelHandlerContext context) {
        Map<String, ChannelHandlerContext> typeConnMap = serverTypeConnMap.computeIfAbsent(serverType, (key) -> new HashMap<>());
        typeConnMap.put(serviceId, context);
    }

    public Map<String, ChannelHandlerContext> getServerConnsByType(int serverType) {
        return serverTypeConnMap.get(serverType);
    }

    public ChannelHandlerContext getServerConnByServerInfo(ServerNodeInfo serverNodeInfo) {
        if (serverNodeInfo == null) {
            return null;
        }
        Map<String, ChannelHandlerContext> serverTupeConnMaps = serverTypeConnMap.get(serverNodeInfo.getServerType());
        if (serverTupeConnMaps == null) {
            return null;
        }
        return serverTupeConnMaps.get(serverNodeInfo.getServiceId());
    }

    public void delete(ServerNodeInfo serverNodeInfo) {
        Map<String, ChannelHandlerContext> serverTypeConnMaps = serverTypeConnMap.get(serverNodeInfo.getServerType());
        if (serverTypeConnMaps == null) {
            log.error("serverNodeInfo ={} not found connection", serverNodeInfo);
            return;
        }
        ChannelHandlerContext channelHandlerContext = serverTypeConnMaps.remove(serverNodeInfo.getServiceId());
        if (channelHandlerContext == null) {
            log.error("serverNodeInfo ={} not found connection", serverNodeInfo);
            return;
        }
        if (serverTypeConnMaps.isEmpty()) {
            serverTypeConnMap.remove(serverNodeInfo.getServerType());
        }
    }

    private void send(int serverType, long roleId, int msgId, Message body) {
        Map<String, ChannelHandlerContext> channelHandlerContextMap = serverTypeConnMap.get(serverType);
        ChannelHandlerContext channelHandlerContext = loadBalance(serverType, roleId, channelHandlerContextMap);
        if (channelHandlerContext == null) {
            log.error("=====error  serverType ={} no connection  sendMsgId ={} roleId ={}", serverType, msgId, roleId);
            return;
        }
        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().setRoleId(roleId).build();
        AresPacket aresPacket = AresPacket.create(msgId, header, body);
        channelHandlerContext.writeAndFlush(aresPacket);
    }

    private void innerRedirectTo(int serverType, long roleId, AresPacket aresPacket) {
        Map<String, ChannelHandlerContext> channelHandlerContextMap = serverTypeConnMap.get(serverType);
        ChannelHandlerContext channelHandlerContext = loadBalance(serverType, roleId, channelHandlerContextMap);
        if (channelHandlerContext == null) {
            log.error("=====error  serverType ={} no connection  sendMsgId ={} roleId ={}", serverType, aresPacket.getMsgId(), roleId);
            return;
        }
        doInnerRedirectTo(channelHandlerContext, roleId, aresPacket);
    }

    //This may be overwritten by gateway  only called in io thread
    protected void doInnerRedirectTo(ChannelHandlerContext channelHandlerContext, long roleId, AresPacket aresPacket) {
        aresPacket.getRecvByteBuf().readerIndex(0);
        channelHandlerContext.writeAndFlush(aresPacket.getRecvByteBuf().retain());
    }
}
