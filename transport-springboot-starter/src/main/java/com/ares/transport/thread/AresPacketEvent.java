package com.ares.transport.thread;

import com.ares.transport.context.AresTKcpContextEx;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AresPacketEvent {
    private AresTKcpContextEx packet;
}
