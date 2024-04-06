package com.ares.gateway.network;


import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.gateway.discovery.OnDiscoveryWatchService;
import com.ares.transport.bean.NetWorkConstants;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.peer.PeerConnBase;
import com.game.protoGen.ProtoInner;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class PeerConn  extends PeerConnBase {
    @Autowired
    private OnDiscoveryWatchService onDiscoveryWatchService;
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
        ServerNodeInfo lowerLoadServerNodeInfo = onDiscoveryWatchService.getLowerLoadServerNodeInfo(serverType);
        ChannelHandlerContext context = getServerConnByServerInfo(lowerLoadServerNodeInfo);
        playerIdContext.put(roleId, context);
        return context;
    }

    @Override
    protected void doInnerRedirectTo(ChannelHandlerContext channelHandlerContext, long roleId, AresPacket aresPacket) {
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
    }

}
