package com.ares.transport.client;

import com.ares.core.bean.AresPacket;
import com.google.protobuf.Message;

public interface AresTcpClient {
    void send(int msgId, int serverId, Message body);

    void send(int serverId, AresPacket... packets);

    void send(int serverId, AresPacket packet);

    void init();
}
