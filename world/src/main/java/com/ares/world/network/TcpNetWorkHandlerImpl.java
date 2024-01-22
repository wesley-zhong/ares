package com.ares.world.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.TcpNetWorkHandler;
import com.ares.transport.client.AresTcpClient;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Note: TcpNetWorkHandlerImpl  worked on IO thread
 */

@Component
@Slf4j
public class TcpNetWorkHandlerImpl implements TcpNetWorkHandler {

    @Override
    public void handleMsgRcv(AresPacket aresPacket) {

    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {

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
