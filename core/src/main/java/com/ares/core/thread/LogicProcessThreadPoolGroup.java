package com.ares.core.thread;

import com.ares.core.tcp.AresTcpHandler;
import lombok.extern.slf4j.Slf4j;

// 1 epoll event thread 1  ServerRpcProcessThreadPoolGroup
@Slf4j
public class LogicProcessThreadPoolGroup {

    static final int MAXIMUM_CAPACITY = 1 << 30;
    public static LogicProcessThreadPoolGroup INSTANCE;
    private LogicProcessThreadPool[] serverRpcProcessThreadPools;
    private final int threadCount;

    public static LogicProcessThreadPoolGroup create(int eventThreadCount, AresTcpHandler aresRpcHandler, int logicAysnThreadCount) {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new LogicProcessThreadPoolGroup(eventThreadCount, logicAysnThreadCount);
        return INSTANCE;
    }

    public LogicProcessThreadPoolGroup(int eventThreadCount, int logicAysnThreadCount) {
        if (logicAysnThreadCount == 0) { // no asyn thread it should be synchronize call
            threadCount = 1;
        } else {
            threadCount = threadSizeFor(eventThreadCount);
        }
        serverRpcProcessThreadPools = new LogicProcessThreadPool[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            serverRpcProcessThreadPools[i] = LogicProcessThreadPool.create(logicAysnThreadCount);
        }
    }

    public LogicProcessThreadPool getThreadPoolByThreadId() {
        if (threadCount == 1) {//this was not a thread pool it should be synchronize call
            return serverRpcProcessThreadPools[0];
        }
        long curThreadId = Thread.currentThread().getId();
        int index = hash(curThreadId) & (threadCount - 1);
        long usedThreadId = serverRpcProcessThreadPools[index].getThreadId();
        if (curThreadId == usedThreadId) {
            return serverRpcProcessThreadPools[index];
        }
        synchronized (this) {
            usedThreadId = serverRpcProcessThreadPools[index].getThreadId();
            while (usedThreadId != curThreadId) {
                if (usedThreadId == 0L) {
                    serverRpcProcessThreadPools[index].setThreadId(curThreadId);
                    return serverRpcProcessThreadPools[index];
                }
                index = (index + 1) & (threadCount - 1);
                usedThreadId = serverRpcProcessThreadPools[index].getThreadId();
            }
        }
        return serverRpcProcessThreadPools[index];
    }

    static final int threadSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }


    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    public void shutDown() {
        for (LogicProcessThreadPool serverRpcProcessThreadPool : serverRpcProcessThreadPools) {
            if (serverRpcProcessThreadPool != null) {
                serverRpcProcessThreadPool.shutDown();
            }
        }
    }
}
