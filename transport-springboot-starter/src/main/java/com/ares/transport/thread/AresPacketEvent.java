package com.ares.transport.thread;

import com.ares.transport.context.AresTcpContextEx;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AresPacketEvent {
    private AresTcpContextEx packet;
}
