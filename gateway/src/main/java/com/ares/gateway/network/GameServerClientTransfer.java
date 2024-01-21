package com.ares.gateway.network;

import com.ares.discovery.annotation.AresAsynTransport;
import com.google.protobuf.Message;

@AresAsynTransport("game.V1")
public interface GameServerClientTransfer {
    void sendMsg(int msgId, Message body);

    void sendMsg(int areaId, int msgId, Message body);
}
