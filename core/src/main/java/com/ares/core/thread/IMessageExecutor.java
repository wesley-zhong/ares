package com.ares.core.thread;


import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.thread.task.EventFunction;
import com.google.protobuf.Message;

import java.util.function.BiFunction;

/**
 * 消息处理器
 */
public interface IMessageExecutor {
    /**
     * 启动消息处理器
     */
    void start();

    /**
     * 停止消息处理器
     */
    void stop();


    /**
     * 判断队列是否已经达到上限了
     *
     * @return
     */
    boolean isFull();


    /**
     * 执行任务
     *
     */
    void execute(AresTKcpContext aresTKcpContext,AresMsgIdMethod method, long param1, Object param2);

    <T> void execute(long id, EventFunction<T> method, long p1, T p2);
}