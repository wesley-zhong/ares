package com.ares.game.network;

import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.bean.AresPacket;
import com.ares.core.exception.AresBaseException;
import com.ares.core.service.ServiceMgr;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.thread.LogicProcessThreadPool;
import com.ares.game.player.GamePlayer;
import com.ares.game.service.PlayerRoleService;
import com.ares.transport.bean.ServerNodeInfo;
import com.ares.transport.bean.TcpConnServerInfo;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoInner;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
public class GameMsgHandler implements AresTcpHandler {
    @Autowired
    protected ServiceMgr serviceMgr;

    @Autowired
    private AresTcpClient aresTcpClient;
    @Value("${spring.application.name}")
    private String appName;


    @Value("${area.id:100}")
    private int areaId;

    @Autowired
    private PeerConn peerConn;

    @Autowired
    private PlayerRoleService playerRoleService;


    protected static final String UTF8 = "UTF-8";

    @Override
    public void handleMsgRcv(AresTKcpContext aresTKcpContext) {
        int length = 0;
        AresPacket aresPacket = aresTKcpContext.getRcvPackage();
        try {
            AresMsgIdMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());
            aresPacket.getRecvByteBuf().skipBytes(6);
            int headerLen = aresPacket.getRecvByteBuf().readShort();
            long pid = 0;
            if (headerLen > 0) {
                ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), headerLen));
                pid = header.getRoleId();
            }

            if (calledMethod == null) {
                if (fromServerType(aresTKcpContext) == ServerType.GATEWAY) {
                    peerConn.directSendToWorld(aresPacket);
                } else {
                    directToGateway(pid, aresPacket);
                }
                return;
            }

            length = aresPacket.getRecvByteBuf().readableBytes();
            Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
            LogicProcessThreadPool.INSTANCE.execute(aresTKcpContext, calledMethod,pid, paraObj);
           // calledMethod.getAresServiceProxy().callMethod(calledMethod, pid, paraObj);
        } catch (AresBaseException e) {
            log.error("===error  length ={} msgId={} ", length, aresPacket.getMsgId(), e);
        } catch (Throwable e) {
            log.error("==error length ={} msgId ={}  ", length, aresPacket.getMsgId(), e);
        }
    }

    private void directToGateway(long pid, AresPacket aresPacket) {
        GamePlayer player = playerRoleService.getPlayer(pid);
        if (player == null) {
            log.error("pid = {} not found", pid);
            return;
        }
        ByteBuf sendBody = aresPacket.getRecvByteBuf().retain();
        sendBody.readerIndex(0);
        player.send(sendBody);
        log.info("-------------------- direct to gateway pid ={} msgId={} ", pid, aresPacket.getMsgId());
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
        ProtoInner.InnerServerHandShakeReq handleShake = ProtoInner.InnerServerHandShakeReq.newBuilder()
                .setAreaId(areaId)
                .setServiceName(appName).build();

        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().build();
        AresPacket aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_REQ_VALUE, header, handleShake);
        aresTKcpContext.writeAndFlush(aresPacket);
        log.info("###### send to {} handshake msg: {}", aresTKcpContext, handleShake);
    }

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
