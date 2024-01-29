package com.ares.core.tcp;

import io.netty.channel.Channel;

public interface AresTcpHandler {

    void handleMsgRcv(AresTKcpContext aresPacket);

    void onClientConnected(AresTKcpContext aresTKcpContext);

    void onClientClosed(AresTKcpContext aresTKcpContext);

    boolean isChannelValidate(AresTKcpContext aresTKcpContext);

    void onServerConnected(Channel aresTKcpContext);

    void onServerClosed(Channel aresTKcpContext);
}

