package com.ares.transport.thread;

import com.ares.transport.context.AresTKcpContextImplEx;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AresPacketEvent {
    private AresTKcpContextImplEx packet;
}
