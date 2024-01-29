package com.ares.core.thread;

import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.tcp.AresTKcpContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogicProcessThreadPool {
    private int processThreadCount;
    private IMessageExecutor[] iMessageExecutors;

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    private volatile long threadId;

    public  static LogicProcessThreadPool INSTANCE = new LogicProcessThreadPool(1);


    public static LogicProcessThreadPool create(int logicAysnThreadCount) {
        return new LogicProcessThreadPool(logicAysnThreadCount);
    }


    public LogicProcessThreadPool(int logicAysnThreadCount) {
        processThreadCount = logicAysnThreadCount;
        iMessageExecutors = new IMessageExecutor[processThreadCount];
        AresThreadFactory aresThreadFactory = new AresThreadFactory("a-l-t-P-");
        for (int i = 0; i < logicAysnThreadCount; ++i) {
            iMessageExecutors[i] = new DisruptorSingleExecutor(aresThreadFactory);
            iMessageExecutors[i].start();
        }
    }

    public void execute(AresTKcpContext aresTKcpContext, AresMsgIdMethod method, long param1, Object param2) {
        try {
            IMessageExecutor iMessageExecutor = getChannelIMessageExecutor();
            iMessageExecutor.execute(aresTKcpContext,method, param1, param2);
        } catch (Exception e) {
            log.error("e99999999999 ", e);
        }
    }


    public void shutDown() {
        if (iMessageExecutors == null) {
            return;
        }
        for (IMessageExecutor iMessageExecutor : iMessageExecutors) {
            iMessageExecutor.stop();
        }
    }

    private IMessageExecutor getChannelIMessageExecutor() {
//        return iMessageExecutors[(processThreadCount - 1) & hash(aresPacket.getCtx().channel())];
        //only for one thread
        return iMessageExecutors[(processThreadCount - 1)];
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
}
