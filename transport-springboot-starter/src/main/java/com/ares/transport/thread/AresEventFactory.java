package com.ares.transport.thread;

import com.lmax.disruptor.EventFactory;

public class AresEventFactory implements EventFactory<AresPacketEvent> {
    @Override
    public AresPacketEvent newInstance() {
        return new AresPacketEvent();
    }
}
