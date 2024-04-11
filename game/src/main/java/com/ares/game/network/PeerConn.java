package com.ares.game.network;


import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresPacket;
import com.ares.game.discovery.OnDiscoveryWatchService;
import com.ares.transport.bean.NetWorkConstants;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.peer.PeerConnBase;
import com.game.protoGen.ProtoInner;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
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

    //send msg to team byu router server
    public void routerToTeam(long roleId, int msgId, Message body) {
        routerTo(ServerType.TEAM, roleId,msgId, body);
    }

    //send to msg to other game server by router server
    public void routerToOtherGame(long roleId, int msgId, Message body) {
        routerTo(ServerType.GAME, roleId,msgId, body);
    }

    //send msg to gateway
    public void sendGateWayMsg(long roleId, int msgId, Message body) {
        send(ServerType.GATEWAY, roleId, msgId, body);
    }

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
        if (channelHandlerContext == null) {
            log.warn(" server type={} roleId = {} something error", serverType, roleId);
            return null;
        }
        ServerNodeInfo lowerLoadServerNodeInfo = onDiscoveryWatchService.getLowerLoadServerNodeInfo(serverType);
        ChannelHandlerContext context = getServerConnByServerInfo(lowerLoadServerNodeInfo);
        if (context == null) {
            return null;
        }
        channelHandlerContext.setContext(serverType, context);
        return context;
    }

    /**
     * 从gateway 接收到的消息，直接转发 那么只有一个目的地  team server
     * @param roleId
     * @param aresPacket
     */

    //   the msg to team  from network io thread and then proxy the msg to router server and proxy it to the team server.
    //  this can not be called by logic
    public void redirectRouterToTeam(long roleId, AresPacket aresPacket) {
        ProtoInner.InnerMsgHeader innerHeader = ProtoInner.InnerMsgHeader.newBuilder()
                .setRouterTo(ServerType.TEAM.getValue())
                .setRoleId(roleId).build();
        //|body|
        int readableBytes = aresPacket.getRecvByteBuf().readableBytes();
        byte[] header = innerHeader.toByteArray();
        //send body |msgLen->4|msgId->2|headerLen->2|headerBody|body
        //totalLen  do not include 4bytes msgLen
        int totalLen = readableBytes + NetWorkConstants.MSG_ID_BYTES + NetWorkConstants.INNER_MSG_LEN_BYTES + header.length;

        CompositeByteBuf byteBufs = ByteBufAllocator.DEFAULT.compositeDirectBuffer();
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(NetWorkConstants.MSG_LEN_BYTES + NetWorkConstants.MSG_ID_BYTES + NetWorkConstants.INNER_MSG_LEN_BYTES + header.length);
        buffer.writeInt(totalLen);
        buffer.writeShort(aresPacket.getMsgId())
                .writeShort(header.length).writeBytes(header);

        byteBufs.addComponents(true, buffer, aresPacket.getRecvByteBuf().retain());
        innerRedirectTo(ServerType.ROUTER, roleId, byteBufs);
    }


    //  the msg to gateway  from network io thread proxy the msg to gateway then to send to client .
    // this can not be called by logic
    public void redirectToGateway(long pid, AresPacket aresPacket) {
        innerRedirectTo(ServerType.GATEWAY, pid, aresPacket);
    }
}
