package com.ares.gateway.network;

import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.bean.AresPacket;
import com.ares.core.exception.AresBaseException;
import com.ares.core.service.ServiceMgr;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.thread.LogicProcessThreadPool;
import com.ares.discovery.DiscoveryService;
import com.ares.gateway.bean.PlayerSession;
import com.ares.gateway.service.SessionService;
import com.ares.transport.bean.NetWorkConstants;
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
public class GateWayMsgHandler implements AresTcpHandler {
    @Autowired
    private ServiceMgr serviceMgr;
    @Autowired
    private AresTcpClient aresTcpClient;
    @Autowired
    private PeerConn peerConn;

    @Autowired
    private SessionService sessionService;
    @Autowired
    private DiscoveryService discoveryService;


    @Override
    public void handleMsgRcv(AresTKcpContext aresTKcpContext) {
        int length = 0;
        AresPacket aresPacket = aresTKcpContext.getRcvPackage();
        try {
            AresMsgIdMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());
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

                // if not processed by myself
                if (calledMethod == null) {
                    directSendToClient(roleId, aresPacket, headerLen);
                    return;
                }
            } else if (aresTKcpContext.getCacheObj() instanceof PlayerSession playerSession) {
                //come from client
                // if not processed by myself
                if (calledMethod == null) {
                    directSendGame(playerSession, aresPacket);
                    return;
                }
            }
            //process by myself
            length = aresPacket.getRecvByteBuf().readableBytes();
            Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
            LogicProcessThreadPool.INSTANCE.execute(aresTKcpContext, calledMethod, roleId, paraObj);

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
        /**
         *  body =|totalLen->4|msgId->2|headerLen->2|headerBody|body|
         *  sendMsgLen = totalLen - NetWorkConstants.INNER_MSG_LEN_BYTES - headerLen;
         *  sendBody =|sendMsgLen->4|msgId->2|body|
         */

        int sendMsgLen = totalLen - NetWorkConstants.INNER_MSG_LEN_BYTES - headerLen;
        body.skipBytes(headerLen + NetWorkConstants.INNER_MSG_LEN_BYTES );

        body.setInt(headerLen + NetWorkConstants.INNER_MSG_LEN_BYTES, sendMsgLen);
        body.setShort(headerLen +  NetWorkConstants.INNER_MSG_LEN_BYTES + NetWorkConstants.MSG_LEN_BYTES, aresPacket.getMsgId());
        body.retain();
        sessionService.sendPlayerMsg(roleId, body);
        //  log.info("------- direct send to client msg roleId ={} msgId={}", roleId, aresPacket.getMsgId());
    }

    private void directSendGame(PlayerSession playerSession, AresPacket aresPacket) {
     //   peerConn.redirectToGameMsg(playerSession.getAreaId(), playerSession.getRoleId(), aresPacket);
        peerConn.innerRedirectTo(ServerType.GAME, playerSession.getRoleId(), aresPacket);
    }

    //connect to the server call back
    @Override
    public void onServerConnected(Channel aresTKcpContext) {
        ServerNodeInfo myNodeInfo = discoveryService.getEtcdRegister().getMyselfNodeInfo();
        ProtoInner.InnerServerHandShakeReq handleShake = ProtoInner.InnerServerHandShakeReq.newBuilder()
                .setServiceName(myNodeInfo.getServiceId()).build();

        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().build();
        AresPacket aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_REQ_VALUE, header, handleShake);
        aresTKcpContext.writeAndFlush(aresPacket);
        log.info("######  handshake send to {}  msg: {}", aresTKcpContext, handleShake);
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
            peerConn.sendToGameMsg( playerSession.getRoleId(), ProtoInner.InnerProtoCode.INNER_PLAYER_DISCONNECT_REQ_VALUE, disconnectRequest);
            LogicProcessThreadPool.INSTANCE.execute(0, playerSession, sessionService::playerDisconnect);
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
