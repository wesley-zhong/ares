package com.ares.transport.peer;

import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.transport.bean.ServerNodeInfo;
import com.game.protoGen.ProtoCommon;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
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

    public void innerRedirectTo(ServerType serverType, long roleId, ByteBuf byteBuf) {
        innerRedirectTo(serverType.getValue(), roleId, byteBuf);
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
            log.error("===== error  serverType ={} no connection  sendMsgId ={} roleId ={}", serverType, msgId, roleId);
            return;
        }
        ProtoCommon.MsgHeader header = ProtoCommon.MsgHeader.newBuilder()
                .setMsgId(msgId)
                .setRoleId(roleId).build();
        AresPacket aresPacket = AresPacket.create(header, body);
        channelHandlerContext.writeAndFlush(aresPacket);
    }

    /**
     * send the msg to the router server to router to the serverType
     *
     * @param serverType to the target server
     * @param roleId     roleId
     * @param msgId      msgId
     * @param body       body
     */
    protected void routerTo(ServerType serverType, long roleId, int msgId, Message body) {
        Map<String, ChannelHandlerContext> channelHandlerContextMap = serverTypeConnMap.get(ServerType.ROUTER.getValue());
        ChannelHandlerContext channelHandlerContext = loadBalance(ServerType.ROUTER.getValue(), roleId, channelHandlerContextMap);
        if (channelHandlerContext == null) {
            log.error("=== ==error  serverType ={} no connection  sendMsgId ={} roleId ={}", ServerType.ROUTER, msgId, roleId);
            return;
        }
        ProtoCommon.MsgHeader header = ProtoCommon.MsgHeader.newBuilder()
                .setRoleId(roleId)
                .setMsgId(msgId)
                .setRouterTo(serverType.getValue()).build();
        AresPacket aresPacket = AresPacket.create(header, body);
        channelHandlerContext.writeAndFlush(aresPacket);
    }

    private void innerRedirectTo(int serverType, long roleId, AresPacket aresPacket) {
        Map<String, ChannelHandlerContext> channelHandlerContextMap = serverTypeConnMap.get(serverType);
        ChannelHandlerContext channelHandlerContext = loadBalance(serverType, roleId, channelHandlerContextMap);
        if (channelHandlerContext == null) {
            log.error("=====error  serverType ={} no connection  sendMsgId ={} roleId ={}", serverType, aresPacket.getMsgId(), roleId);
            return;
        }
        doInnerRedirectTo(serverType, channelHandlerContext, roleId, aresPacket);
    }

    private void innerRedirectTo(int serverType, long roleId, ByteBuf body) {
        Map<String, ChannelHandlerContext> channelHandlerContextMap = serverTypeConnMap.get(serverType);
        ChannelHandlerContext channelHandlerContext = loadBalance(serverType, roleId, channelHandlerContextMap);
        if (channelHandlerContext == null) {
            log.error("=====error  serverType ={} no connection  roleId ={}", serverType, roleId);
            return;
        }
        channelHandlerContext.writeAndFlush(body);
    }

    //This may be overwritten by gateway  only called in io thread
    protected void doInnerRedirectTo(int serverType, ChannelHandlerContext channelHandlerContext, long roleId, AresPacket aresPacket) {
        ProtoCommon.MsgHeader innerMsgHeader = aresPacket.getRecvHeader().toBuilder().setRoleId(roleId).build();
        //|body|
        int readableBytes = 0;
        if (aresPacket.getRecvByteBuf() != null) {
            readableBytes = aresPacket.getRecvByteBuf().readableBytes();
        }
        byte[] header = innerMsgHeader.toByteArray();
        int totalLen = 1 + readableBytes + header.length;

        CompositeByteBuf byteBufs = ByteBufAllocator.DEFAULT.compositeDirectBuffer();
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(header.length + 1 + 4);
        buffer.writeInt(totalLen)
                .writeByte(header.length)
                .writeBytes(header);

        byteBufs.addComponents(true, buffer, aresPacket.getRecvByteBuf().retain());
        channelHandlerContext.writeAndFlush(byteBufs);
    }
}
