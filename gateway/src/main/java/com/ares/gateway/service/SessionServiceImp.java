package com.ares.gateway.service;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.discovery.DiscoveryService;
import com.ares.gateway.bean.PlayerSession;
import com.ares.gateway.network.PeerConn;
import com.ares.transport.bean.ServerNodeInfo;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import com.google.protobuf.Message;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class SessionServiceImp implements SessionService {
    @Autowired
    private PeerConn peerConn;
    @Autowired
    private DiscoveryService discoveryService;

    private final Map<Long, AresTKcpContext> playerChannelContext = new ConcurrentHashMap<>();

    @Override
    public void loginRequest(AresTKcpContext aresTKcpContext, ProtoTask.LoginRequest loginRequest) {
        log.info(" gateway==============loginRequest:{}", loginRequest);
        /****
         * do something
         */
        boolean bValid = checkPlayerToken(loginRequest.getRoleId(), loginRequest.getLoginToken());
        if (!bValid) {
            ProtoTask.LoginResponse response = ProtoTask.LoginResponse.newBuilder()
                    .setRoleId(loginRequest.getRoleId())
                    .setServerTime(System.currentTimeMillis())
                    .setErrorCode(ProtoCommon.ProtoError.INVALID_LOGIN_TOKEN_VALUE).build();
            aresTKcpContext.send(AresPacket.create(ProtoCommon.ProtoCode.LOGIN_RESPONSE_VALUE, response));
            return;
        }


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
        //update online count to etcd
        updateMyNodeInfo();
    }

    @Override
    public void playerDisconnect(PlayerSession playerSession) {
        log.info("-------------  player disconnect ={}", playerSession);
        playerChannelContext.remove(playerSession.getRoleId());

        //update online count to etcd
        updateMyNodeInfo();
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
            log.error("roleId = {}  not found in gateway", roleId);
            return;
        }
        channelHandlerContext.send(body);
    }


    @Override
    public AresTKcpContext getRoleContext(long roleId) {
        return playerChannelContext.get(roleId);
    }

    private boolean checkPlayerToken(long roleId, String token) {
//        try {
//            GetResponse getResponse = discoveryService.getEtcdClient().getKVClient()
//                    .get(ByteSequence.from(roleId + "", StandardCharsets.UTF_8)).get();
//            if(getResponse.getCount() != 1){
//                return false;
//            }
//            KeyValue keyValue = getResponse.getKvs().get(0);
//            return  keyValue.getValue().toString(StandardCharsets.UTF_8).equals(token);
//        } catch (Exception e) {
//            log.error("-----error", e);
//        }
        return true;

    }

    private void updateMyNodeInfo(){
        ServerNodeInfo myNodeInfo = discoveryService.getEtcdRegister().getMyNodeInfo();
        int online =  playerChannelContext.size();
        myNodeInfo.getMetaData().put("online",online+"");
        discoveryService.getEtcdRegister().updateServerNodeInfo(myNodeInfo );
    }
}
