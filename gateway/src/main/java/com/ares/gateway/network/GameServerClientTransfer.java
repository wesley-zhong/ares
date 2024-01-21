package com.ares.gateway.network;

import com.ares.discovery.annotation.AresAsynTransport;
import com.ares.discovery.transfer.AresServerTransfer;
import com.google.protobuf.Message;

@AresAsynTransport("game.V1")
public interface GameServerClientTransfer extends AresServerTransfer {
}
