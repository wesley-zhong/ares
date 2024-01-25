package com.ares.gateway.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.TcpNetWorkHandler;
import com.ares.core.utils.AresContextThreadLocal;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Note: TcpNetWorkHandlerImpl  worked on IO thread
 */

@Component
@Slf4j
public class TcpNetWorkHandlerImpl implements TcpNetWorkHandler {
    @Value("${spring.application.name}")
    private String appName;

    @Value("${area.id:100}")
    private int areaId;

    @Autowired
    private GameServerClientTransfer gameServerClientTransfer;

    @Override
    public void handleMsgRcv(AresPacket aresPacket) {
        AresTKcpContext aresTKcpContext = AresContextThreadLocal.get();
        aresTKcpContext.getCacheObj();

        gameServerClientTransfer.sendMsg(areaId, aresPacket);
    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {
        ProtoInner.InnerServerHandShakeReq handleShake = ProtoInner.InnerServerHandShakeReq.newBuilder()
                .setAreaId(areaId)
                .setServiceName(appName).build();

        AresPacket aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_REQ_VALUE, handleShake);
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
