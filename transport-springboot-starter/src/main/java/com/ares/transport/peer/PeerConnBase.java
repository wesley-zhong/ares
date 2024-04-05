package com.ares.transport.peer;

import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.transport.bean.NetWorkConstants;
import com.ares.transport.bean.ServerNodeInfo;
import com.game.protoGen.ProtoInner;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PeerConnBase {
    /**
     * Integer key : server type
     * every server process has one connection, there may be many server processes with the same server type
     * String key : service_Id
     *
     */
    private final Map<Integer, Map<String, ChannelHandlerContext>> serverTypeConnMap = new ConcurrentHashMap<>();

    public void addServerConn(ServerNodeInfo serverNodeInfo, ChannelHandlerContext context){
        Map<String, ChannelHandlerContext> typeConnMap = serverTypeConnMap.computeIfAbsent(serverNodeInfo.getServerType(), (key)->new HashMap<>());
        typeConnMap.put(serverNodeInfo.getServiceId(), context);
    }
    public Map<String, ChannelHandlerContext> getServerConnsByType(int serverType){
        return serverTypeConnMap.get(serverType);
    }
    public ChannelHandlerContext getServerConnByServerInfo(ServerNodeInfo serverNodeInfo){
        Map<String, ChannelHandlerContext> serverTupeConnMaps = serverTypeConnMap.get(serverNodeInfo.getServerType());
        if(serverTupeConnMaps == null){
            return null;
        }
        return serverTupeConnMaps.get(serverNodeInfo.getServiceId());
    }
    public void delete(ServerNodeInfo serverNodeInfo){
        Map<String, ChannelHandlerContext> serverTupeConnMaps = serverTypeConnMap.get(serverNodeInfo.getServerType());
        if(serverTupeConnMaps == null){
            log.error("serverNodeInfo ={} not found connection", serverNodeInfo);
            return;
        }
        ChannelHandlerContext channelHandlerContext = serverTupeConnMaps.remove(serverNodeInfo.getServiceId());
        if(channelHandlerContext == null){
            log.error("serverNodeInfo ={} not found connection", serverNodeInfo);
            return;
        }
        if(serverTupeConnMaps.isEmpty()){
            serverTypeConnMap.remove(serverNodeInfo.getServerType());
        }
    }

    public void innerRedirectTo(ChannelHandlerContext channelHandlerContext, long roleId, AresPacket aresPacket) {
        ProtoInner.InnerMsgHeader build = ProtoInner.InnerMsgHeader.newBuilder().setRoleId(roleId).build();
        //|body|
        int readableBytes = aresPacket.getRecvByteBuf().readableBytes();
        byte[] header = build.toByteArray();
        //send body |msgLen->4|msgId->2|headerLen->2|headerBody|body
        //totalLen  do not include 4bytes msgLen
        int totalLen = readableBytes + NetWorkConstants.MSG_ID_BYTES + NetWorkConstants.INNER_MSG_LEN_BYTES + header.length;


        CompositeByteBuf byteBufs = ByteBufAllocator.DEFAULT.compositeDirectBuffer();

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(NetWorkConstants.MSG_LEN_BYTES + NetWorkConstants.MSG_ID_BYTES + NetWorkConstants.INNER_MSG_LEN_BYTES + header.length);
        buffer.writeInt(totalLen);
        buffer.writeShort(aresPacket.getMsgId())
                .writeShort(header.length).writeBytes(header);

        byteBufs.addComponents(true, buffer, aresPacket.getRecvByteBuf().retain());
        channelHandlerContext.writeAndFlush(byteBufs);
        //   log.info("-----direct msg to game game server roleId ={} msgId ={} areaId={}", roleId, aresPacket.getMsgId(), areaId);
    }


}
