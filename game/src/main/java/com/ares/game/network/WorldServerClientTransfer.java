package com.ares.game.network;

import com.ares.discovery.annotation.AresAsynTransport;
import com.ares.discovery.transfer.AresServerTransfer;

@AresAsynTransport("world.V1")
public interface WorldServerClientTransfer extends AresServerTransfer {
}
