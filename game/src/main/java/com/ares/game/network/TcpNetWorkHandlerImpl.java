package com.ares.game.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.TcpNetWorkHandler;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoInner;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Note: TcpNetWorkHandlerImpl  worked on IO thread
 */

@Component
@Slf4j
public class TcpNetWorkHandlerImpl implements TcpNetWorkHandler {
    @Autowired
    private AresTcpClient aresTcpClient;
    @Value("${spring.application.name}")
    private String appName;


    @Value("${area.id:100}")
    private int areaId;

    @Override
    public void handleMsgRcv(AresPacket aresPacket) {
        log.info("XXXXXXXXXXXXXXX  handler msg recv id ={}", aresPacket.getMsgId());

    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {
        ProtoInner.InnerServerHandShake handleShake = ProtoInner.InnerServerHandShake.newBuilder()
                .setAreaId(areaId)
                .setServiceName(appName).build();

        AresPacket  aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_VALUE, handleShake);
        aresTKcpContext.writeAndFlush(aresPacket);
        log.info("###### send to {} handshake msg: {}",aresTKcpContext, handleShake);
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
