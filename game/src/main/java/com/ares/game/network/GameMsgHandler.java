package com.ares.game.network;

import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.bean.AresPacket;
import com.ares.core.service.ServiceMgr;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.thread.LogicProcessThreadPool;
import com.ares.discovery.DiscoveryService;
import com.ares.game.service.PlayerRoleService;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.bean.TcpConnServerInfo;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoInner;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


@Slf4j
public class GameMsgHandler implements AresTcpHandler {
    @Autowired
    protected ServiceMgr serviceMgr;

    @Autowired
    private AresTcpClient aresTcpClient;

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private PeerConn peerConn;

    @Autowired
    private PlayerRoleService playerRoleService;


    protected static final String UTF8 = "UTF-8";

    @Override
    public void handleMsgRcv(AresTKcpContext aresTKcpContext) throws IOException {
        AresPacket aresPacket = aresTKcpContext.getRcvPackage();
        AresMsgIdMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());
        aresPacket.getRecvByteBuf().skipBytes(6);
        int headerLen = aresPacket.getRecvByteBuf().readShort();
        long pid = 0;
        if (headerLen > 0) {
            ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), headerLen));
            pid = header.getRoleId();
        }

        // no msg method call should proxy to others
        if (calledMethod == null) {
            if (fromServerType(aresTKcpContext) == ServerType.GATEWAY) {
                peerConn.redirectRouterToTeam(pid, aresPacket);
            } else {//this should be from router server
                directToGateway(pid, aresPacket);
            }
            return;
        }

        int length = aresPacket.getRecvByteBuf().readableBytes();
        Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
        LogicProcessThreadPool.INSTANCE.execute(aresTKcpContext, calledMethod, pid, paraObj);
    }

    private void directToGateway(long pid, AresPacket aresPacket) {
        peerConn.redirectToGateway(pid, aresPacket);
    }

    private ServerType fromServerType(AresTKcpContext aresTKcpContext) {
        Object cacheObj = aresTKcpContext.getCacheObj();
        if (cacheObj instanceof TcpConnServerInfo tcpConnServerInfo) {
            ServerNodeInfo serverNodeInfo = tcpConnServerInfo.getServerNodeInfo();
            return ServerType.from(serverNodeInfo.getServiceName());
        }
        return null;
    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {
        ServerNodeInfo myselfNodeInfo = discoveryService.getEtcdRegister().getMyselfNodeInfo();
        ProtoInner.InnerServerHandShakeReq handleShake = ProtoInner.InnerServerHandShakeReq.newBuilder()
                .setServiceId(myselfNodeInfo.getServiceId())
                .setServiceName(myselfNodeInfo.getServiceName()).build();

        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().build();
        AresPacket aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_REQ_VALUE, header, handleShake);
        aresTKcpContext.writeAndFlush(aresPacket);
        log.info("###### send handshake send to {}  msg: {}", aresTKcpContext, handleShake);
    }

    //work as server when client connected  me (this server)
    @Override
    public void onClientConnected(AresTKcpContext aresTKcpContext) {
        log.info("---onClientConnected ={} ", aresTKcpContext);
    }

    @Override
    public void onClientClosed(AresTKcpContext aresTKcpContext) {
        log.info("-----onClientClosed={} ", aresTKcpContext);
    }

    @Override
    public boolean isChannelValidate(AresTKcpContext aresTKcpContext) {
        return true;
    }


    @Override
    public void onServerClosed(Channel aresTKcpContext) {
    }
}
