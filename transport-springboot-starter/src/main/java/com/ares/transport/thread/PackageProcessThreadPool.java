package com.ares.transport.thread;

import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.thread.AresThreadFactory;
import com.ares.transport.context.AresTcpContextEx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PackageProcessThreadPool {
    private int processThreadCount;
    private IMessageExecutor[] iMessageExecutors;
    AresEventHandler aresEventHandler;

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    private volatile long threadId;


    public static PackageProcessThreadPool create(AresTcpHandler aresRpcHandler, int logicAysnThreadCount) {
        return new PackageProcessThreadPool(aresRpcHandler, logicAysnThreadCount);
    }


    public PackageProcessThreadPool(AresTcpHandler aresRpcHandler, int logicAysnThreadCount) {
        processThreadCount = logicAysnThreadCount;
        if (processThreadCount == 0) {
            aresEventHandler = new AresEventHandler(aresRpcHandler);
            return;
        }

        iMessageExecutors = new IMessageExecutor[processThreadCount];
        AresThreadFactory aresThreadFactory = new AresThreadFactory("ares-tcp-thread-pool-");
        for (int i = 0; i < logicAysnThreadCount; ++i) {
            iMessageExecutors[i] = new DisruptorSingleExecutor(aresRpcHandler, aresThreadFactory);
            iMessageExecutors[i].start();
        }
    }

    public void execute(AresTcpContextEx aresPacket) {
        if (processThreadCount == 0) { // do not asyn call
            aresEventHandler.onPacket(aresPacket);
            return;
        }
        try {
            IMessageExecutor iMessageExecutor = getChannelIMessageExecutor(aresPacket);
            iMessageExecutor.execute(aresPacket);
        } catch (Exception e) {
            log.error("e99999999999 ", e);
        }
    }


    public void shutDown() {
        if(iMessageExecutors == null){
            return;
        }
        for (IMessageExecutor iMessageExecutor : iMessageExecutors) {
            iMessageExecutor.stop();
        }
    }

    private IMessageExecutor getChannelIMessageExecutor(AresTcpContextEx aresPacket) {
        return iMessageExecutors[(processThreadCount - 1) & hash(aresPacket.getCtx().channel())];
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
}
