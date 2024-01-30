package com.ares.core.thread;

import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.thread.task.EventFunction;
import com.ares.core.thread.task.PacketEventTask;
import com.ares.core.thread.task.TaskEventTask;
import com.lmax.disruptor.LiteTimeoutBlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DisruptorSingleExecutor implements IMessageExecutor {

    //65536条消息
    private final int MAX_QUE_SIZE = 2 << 15;

    private final RingBuffer<AresEventProcess> ringBuffer;
    private final Disruptor<AresEventProcess> disruptor;


    public DisruptorSingleExecutor(ThreadFactory threadFactory) {
        disruptor = new Disruptor<>(new AresEventFactory(), MAX_QUE_SIZE, threadFactory, ProducerType.MULTI, new LiteTimeoutBlockingWaitStrategy(1, TimeUnit.MILLISECONDS));
        disruptor.handleEventsWith(new AresEventHandler());
        ringBuffer = disruptor.getRingBuffer();
    }


    @Override
    @SuppressWarnings("unchecked")
    public void start() {
        disruptor.start();
    }

    @Override
    public void stop() {
        disruptor.shutdown();
    }


    @Override
    public boolean isFull() {
        return !ringBuffer.hasAvailableCapacity(1);
    }

    @Override
    public void execute(AresTKcpContext aresTKcpContext, AresMsgIdMethod method, long param1, Object param2) {
        try {
            final long sequence = ringBuffer.tryNext();
            try {
                PacketEventTask packetEventTask = new PacketEventTask();
                packetEventTask.setAresTKcpContext(aresTKcpContext);
                packetEventTask.setMethod(method);
                packetEventTask.setParam1(param1);
                packetEventTask.setParam2(param2);
                AresEventProcess aresEventProcess = ringBuffer.get(sequence);
                aresEventProcess.setEventTask(packetEventTask);

            } finally {
                ringBuffer.publish(sequence);
            }
        } catch (Exception e) {
            // This exception is used by the Disruptor as a global goto. It is a singleton
            // and has no stack trace.  Don't worry about performance.
            log.error("Logic thread disruptor buff is error", e);
        }
    }

    @Override
    public <T> void execute(long id, EventFunction<T> method, long p1, T p2) {
        try {
            final long sequence = ringBuffer.tryNext();
            try {
                TaskEventTask<T> taskEventTask = new TaskEventTask<>();
                taskEventTask.setP1(p1);
                taskEventTask.setP2(p2);
                taskEventTask.setFunction(method);
                AresEventProcess aresEventProcess = ringBuffer.get(sequence);
                aresEventProcess.setEventTask(taskEventTask);
            } finally {
                ringBuffer.publish(sequence);
            }
        } catch (Exception e) {
            // This exception is used by the Disruptor as a global goto. It is a singleton
            // and has no stack trace.  Don't worry about performance.
            log.error("Logic thread disruptor buff is error", e);
        }
    }
}
