package com.ares.transport.thread;

import com.ares.core.tcp.AresTcpHandler;
import com.ares.transport.context.AresTcpContextEx;
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

    private final RingBuffer<AresPacketEvent> ringBuffer;
    private final Disruptor<AresPacketEvent> disruptor;


    public DisruptorSingleExecutor(AresTcpHandler aresRpcHandler, ThreadFactory threadFactory) {
        disruptor = new Disruptor<>(new AresEventFactory(), MAX_QUE_SIZE, threadFactory, ProducerType.MULTI, new LiteTimeoutBlockingWaitStrategy(10, TimeUnit.MILLISECONDS));
        disruptor.handleEventsWith(new AresEventHandler(aresRpcHandler));
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
    public void execute(AresTcpContextEx aresPacket) {
        try {
            final long sequence = ringBuffer.tryNext();
            try {
                AresPacketEvent aresPacketEvent = ringBuffer.get(sequence);
                aresPacketEvent.setPacket(aresPacket);
            } finally {
                ringBuffer.publish(sequence);
            }
        } catch (Exception e) {
            // This exception is used by the Disruptor as a global goto. It is a singleton
            // and has no stack trace.  Don't worry about performance.
            log.error("BattleRoomDisruptorThread buff is error", e);
        }
    }
}
