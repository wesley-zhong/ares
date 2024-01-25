package com.ares.gateway.service;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.discovery.EtcdDiscovery;
import com.ares.gateway.network.GameServerClientTransfer;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SessionServiceImp implements  SessionService {
    @Autowired
    private AresTcpClient aresTcpClient;

    private final Map<Long, ChannelHandlerContext > playerChannelContext = new ConcurrentHashMap<>();

    @Override
    public void loginRequest(AresTKcpContext aresTKcpContext ,ProtoTask.LoginRequest loginRequest){
        log.info(" gateway==============loginRequest:{}", loginRequest);
        /****
         * do something
         */
        playerChannelContext.put(loginRequest.getRoleId(), aresTKcpContext.getCtx());

        ProtoInner.InnerGameLoginRequest innerLoginRequest = ProtoInner.InnerGameLoginRequest.newBuilder()
                .setRoleId(loginRequest.getRoleId())
                .setSid(1000).build();

     //   aresTcpClient.send(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE, 100,innerLoginRequest);

        aresTcpClient.send(loginRequest.getAreaId(),"game.V1", ProtoInner.InnerProtoCode.INNER_TO_GAME_LOGIN_REQ_VALUE, innerLoginRequest);
    }

    @Override
    public void sendPlayerMsg(long roleId, int msgId, Message body) {
        ChannelHandlerContext channelHandlerContext = playerChannelContext.get(roleId);
        if(channelHandlerContext  == null){
            log.error("roleId = {}   msgId ={} not login in gateway", roleId, msgId);
            return;
        }
        AresPacket aresPacket = AresPacket.create(msgId, body);
        channelHandlerContext.writeAndFlush(aresPacket);

    }

    @Override
    public void sendPlayerMsg(long roleId, AresPacket aresPacket) {
        ChannelHandlerContext channelHandlerContext = playerChannelContext.get(roleId);
        if(channelHandlerContext  == null){
            log.error("roleId = {}   msgId ={} not login in gateway", roleId, aresPacket.getMsgId());
            return;
        }
        channelHandlerContext.writeAndFlush(aresPacket);
    }
}
