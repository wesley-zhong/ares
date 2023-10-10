package com.ares.gateway.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.TcpNetWorkHandler;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoTask;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Note: TcpNetWorkHandlerImpl  worked on IO thread
 */

@Component
@Slf4j
public class TcpNetWorkHandlerImpl implements TcpNetWorkHandler {
    @Autowired
    @Lazy
    private AresTcpClient aresTcpClient;
    @Override
    public void handleMsgRcv(AresPacket aresPacket) {

    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {

    }

    @Override
    public void onClientConnected(AresTKcpContext aresTKcpContext) {
     log.info("---onClientConnected ={} ", aresTKcpContext);
     aresTcpClient.send(100,35, ProtoTask.LoginResponse.newBuilder().build());
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
