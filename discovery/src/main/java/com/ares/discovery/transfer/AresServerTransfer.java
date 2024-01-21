package com.ares.discovery.transfer;

import com.google.protobuf.Message;

public interface AresServerTransfer {
    void sendMsg(int msgId, Message body);

    void sendMsg(int areaId, int msgId, Message body);
}
