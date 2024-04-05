package com.ares.gateway.network;


import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.transport.bean.NetWorkConstants;
import com.game.protoGen.ProtoInner;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
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
    @Value("${area.id:0}")
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


    public synchronized ChannelHandlerContext getAresTcpContext(ServerType serverType) {
        return getAresTcpContext(areaId, serverType);
    }

    public synchronized ChannelHandlerContext getAresTcpContext(int areaId, ServerType serverType) {
        Map<Integer, ChannelHandlerContext> stringAresTcpContextMap = peerConns.get(areaId);
        if (CollectionUtils.isEmpty(stringAresTcpContextMap)) {
            return null;
        }
        return stringAresTcpContextMap.get(serverType.getValue());
    }

    public void send(int areaId, ServerType serverType, long roleId, int msgId, Message body) {
        ChannelHandlerContext channelHandlerContext = getAresTcpContext(areaId, serverType);
        if (channelHandlerContext == null) {
            log.error("areaId ={} sererType ={}  not found to send msgId ={}", areaId, serverType, msgId);
            return;
        }
        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().setRoleId(roleId).build();
        AresPacket aresPacket = AresPacket.create(msgId, header, body);
        channelHandlerContext.writeAndFlush(aresPacket);
    }


    public void sendToGameMsg(int areaId, long roleId, int msgId, Message body) {
        send(areaId, ServerType.GAME, roleId, msgId, body);
    }

    public void redirectToGameMsg(int areaId, long roleId, AresPacket aresPacket) {
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

        byteBufs.addComponents(true,buffer, aresPacket.getRecvByteBuf().retain());


        ChannelHandlerContext channelHandlerContext = getAresTcpContext(areaId, ServerType.GAME);
        if (channelHandlerContext == null) {
            log.error("areaId ={} sererType ={}  not found to send msgId ={}", areaId, ServerType.GAME, aresPacket.getMsgId());
            return;
        }
        channelHandlerContext.writeAndFlush(byteBufs);
        //   log.info("-----direct msg to game game server roleId ={} msgId ={} areaId={}", roleId, aresPacket.getMsgId(), areaId);
    }

    public void send(ServerType serverType, long roleId, int msgId, Message body) {
        send(areaId, serverType, roleId, msgId, body);
    }
}
