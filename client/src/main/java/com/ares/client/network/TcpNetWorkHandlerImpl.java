package com.ares.client.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.TcpNetWorkHandler;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TcpNetWorkHandlerImpl implements TcpNetWorkHandler {
    @Override
    public void handleMsgRcv(AresPacket aresPacket) {

    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {
        log.info(" onServerConnected  connect {}  sucess ", aresTKcpContext);

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
        log.info("-===== onserver closed ={}", aresTKcpContext);

    }

}
