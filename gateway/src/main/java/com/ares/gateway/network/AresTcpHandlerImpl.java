package com.ares.gateway.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.bean.AresRpcMethod;
import com.ares.core.exception.AresBaseException;
import com.ares.core.service.ServiceMgr;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.gateway.bean.PlayerSession;
import com.ares.gateway.service.SessionService;
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
public class AresTcpHandlerImpl implements AresTcpHandler {

    @Autowired
    private ServiceMgr serviceMgr;
    @Autowired
    private AresTcpClient aresTcpClient;
    @Autowired
    private PeerConn peerConn;
    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private SessionService sessionService;

    @Override
    public void handleMsgRcv(AresPacket aresPacket) {
        int length = 0;
        try {
            AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
            AresRpcMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());
            aresPacket.getRecvByteBuf().skipBytes(6);

            long roleId = 0;
            if (aresTKcpContext.getCacheObj() instanceof TcpConnServerInfo
                    || aresPacket.getMsgId() == ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_RES_VALUE) {
                //come from peer server node
                int headerLen = aresPacket.getRecvByteBuf().readShort();
                if (headerLen > 0) {
                    ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), headerLen));
                    roleId = header.getRoleId();
                }

                if (calledMethod == null) {
                    directSendToClient(roleId, aresPacket, headerLen);
                    return;
                }
            } else if (aresTKcpContext.getCacheObj() instanceof PlayerSession playerSession) {
                //come from client
                if (calledMethod == null) {
                    directSendGame(playerSession, aresPacket);
                    return;
                }
            }
            length = aresPacket.getRecvByteBuf().readableBytes();
            Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
            calledMethod.getAresServiceProxy().callMethod(calledMethod, roleId, paraObj);
        } catch (AresBaseException e) {
            log.error("===error  length ={} msgId={} ", length, aresPacket.getMsgId(), e);
        } catch (Throwable e) {
            log.error("==error length ={} msgId ={}  ", length, aresPacket.getMsgId(), e);
        }
    }


    private void directSendToClient(long roleId, AresPacket aresPacket, int headerLen) {
        ByteBuf body = aresPacket.getRecvByteBuf();
        body.readerIndex(0);
        int totalLen = body.getInt(0);

        int sendMsgLen = totalLen - 2 - headerLen;
        body.skipBytes(headerLen + 2);

        body.setInt(headerLen + 2, sendMsgLen);
        body.setShort(headerLen + 2 + 4, aresPacket.getMsgId());
        body.retain();
        sessionService.sendPlayerMsg(roleId, body);
        log.info("------- direct send to client msg roleId ={} msgId={}", roleId, aresPacket.getMsgId());
    }

    private void directSendGame(PlayerSession playerSession, AresPacket aresPacket) {
        peerConn.redirectToGameMsg(playerSession.getAreaId(), playerSession.getRoleId(), aresPacket);
    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {
        ProtoInner.InnerServerHandShakeReq handleShake = ProtoInner.InnerServerHandShakeReq.newBuilder()
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
        Object cacheObj = aresTKcpContext.getCacheObj();
        if (cacheObj instanceof PlayerSession playerSession) {
            ProtoInner.InnerPlayerDisconnectRequest disconnectRequest = ProtoInner.InnerPlayerDisconnectRequest.newBuilder()
                    .setRoleId(playerSession.getRoleId()).build();
            peerConn.sendToGameMsg(playerSession.getAreaId(), playerSession.getRoleId(), ProtoInner.InnerProtoCode.INNER_PLAYER_DISCONNECT_REQ_VALUE, disconnectRequest);
        }
    }

    @Override
    public boolean isChannelValidate(AresTKcpContext aresTKcpContext) {
        return true;
    }


    @Override
    public void onServerClosed(Channel aresTKcpContext) {

    }
}
