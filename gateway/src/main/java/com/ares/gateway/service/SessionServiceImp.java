package com.ares.gateway.service;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.gateway.bean.PlayerSession;
import com.ares.gateway.network.PeerConn;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SessionServiceImp implements SessionService {
    //    @Autowired
//    private AresTcpClient aresTcpClient;
    @Autowired
    private PeerConn peerConn;

    private final Map<Long, AresTKcpContext> playerChannelContext = new ConcurrentHashMap<>();

    @Override
    public void loginRequest(AresTKcpContext aresTKcpContext, ProtoTask.LoginRequest loginRequest) {
        log.info(" gateway==============loginRequest:{}", loginRequest);
        /****
         * do something
         */
        aresTKcpContext.clearPackageData();
        playerChannelContext.put(loginRequest.getRoleId(), aresTKcpContext);

        ProtoInner.InnerGameLoginRequest innerLoginRequest = ProtoInner.InnerGameLoginRequest.newBuilder()
                .setRoleId(loginRequest.getRoleId())
                .setSid(1000).build();

        peerConn.sendToGameMsg(loginRequest.getAreaId(), loginRequest.getRoleId(), ProtoInner.InnerProtoCode.INNER_TO_GAME_LOGIN_REQ_VALUE, innerLoginRequest);
    }

    @Override
    public void loginSuccess(ProtoInner.InnerGameLoginResponse response) {
        PlayerSession playerSession = new PlayerSession(response.getRoleId(), response.getAreaId());
        AresTKcpContext channelHandlerContext = playerChannelContext.get(response.getRoleId());
        if (channelHandlerContext == null) {
            log.error("roleId = {}    not login in gateway", response.getRoleId());
            return;
        }
        channelHandlerContext.cacheObj(playerSession);

    }

    @Override
    public void sendPlayerMsg(long roleId, int msgId, Message body) {
        AresTKcpContext channelHandlerContext = playerChannelContext.get(roleId);
        if (channelHandlerContext == null) {
            log.error("roleId = {}   msgId ={} not login in gateway", roleId, msgId);
            return;
        }
        AresPacket aresPacket = AresPacket.create(msgId, body);
        channelHandlerContext.send(aresPacket);
    }

    @Override
    public void sendPlayerMsg(long roleId, AresPacket aresPacket) {
        AresTKcpContext channelHandlerContext = playerChannelContext.get(roleId);
        if (channelHandlerContext == null) {
            log.error("roleId = {}   msgId ={} not login in gateway", roleId, aresPacket.getMsgId());
            return;
        }
        channelHandlerContext.send(aresPacket);
        //channelHandlerContext.writeAndFlush(aresPacket);
    }

    @Override
    public void sendPlayerMsg(long roleId, ByteBuf body) {
        AresTKcpContext channelHandlerContext = playerChannelContext.get(roleId);
        if (channelHandlerContext == null) {
            log.error("roleId = {}  not login in gateway", roleId);
            return;
        }
        channelHandlerContext.send(body);
    }

    @Override
    public AresTKcpContext getRoleContext(long roleId) {
        return playerChannelContext.get(roleId);
    }
}
