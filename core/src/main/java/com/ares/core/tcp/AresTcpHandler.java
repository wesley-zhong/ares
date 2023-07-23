package com.ares.core.tcp;

import com.ares.core.bean.AresPacket;
import io.netty.channel.Channel;

public interface AresTcpHandler {

    void handleMsgRcv(AresPacket aresPacket);

    void onClientConnected(AresTKcpContext aresTKcpContext);

    void onClientClosed(AresTKcpContext aresTKcpContext);

    boolean isChannelValidate(AresTKcpContext aresTKcpContext);

    void onServerConnected(Channel aresTKcpContext);

    void onServerClosed(Channel aresTKcpContext);
}

