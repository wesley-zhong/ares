package com.ares.core.thread;

import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.tcp.AresTKcpContext;
import com.google.protobuf.Message;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AresPacketEvent {
    private AresTKcpContext aresTKcpContext;
    private AresMsgIdMethod method;
    private long param1;
    private Object param2;

    public void clear() {
        method = null;
        param1 = 0L;
        param2 = null;
        aresTKcpContext = null;
    }
}
