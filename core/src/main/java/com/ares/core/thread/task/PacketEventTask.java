package com.ares.core.thread.task;

import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.thread.EventTask;
import com.ares.core.utils.AresContextThreadLocal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacketEventTask implements EventTask {
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
    @Override
    public void execute() {
        AresContextThreadLocal.cache(aresTKcpContext);
        method.getAresServiceProxy().callMethod(method,param1,param2);
    }
}
