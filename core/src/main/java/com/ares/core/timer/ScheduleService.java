package com.ares.core.timer;

import com.ares.core.thread.AresThreadFactory;
import com.ares.core.thread.task.EventBiFunction;
import com.ares.core.thread.task.EventFunction;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ScheduleService {
    private final static HashedWheelTimer HASHED_WHEEL_TIMER = new HashedWheelTimer(new AresThreadFactory("ares-timer"), 10, TimeUnit.MILLISECONDS);
    public static ScheduleService INSTANCE;

    private Consumer<AresTimerTask<?>> aresTimerTaskConsumer;

    public ScheduleService(Consumer<AresTimerTask<?>> aresTimerTaskConsumer){
        this.aresTimerTaskConsumer = aresTimerTaskConsumer;
        INSTANCE = this;
    }


    public <T> AresTimerTask<?> executeTimerTaskWithMS(EventFunction<T> function, T extraData, long timeOut) {
        return executeTimerTask(function, extraData, timeOut, TimeUnit.MILLISECONDS);
    }

    public <T> AresTimerTask<?> executeTimerTask(EventFunction<T> function, T extraData, long timeOut, TimeUnit timeUnit) {
        AresTimerTask aresTimerTask = AresTimerTask.NewTimerTask(extraData, function);
        Timeout timeout = HASHED_WHEEL_TIMER.newTimeout(aresTimerTask, timeOut, timeUnit);
        aresTimerTask.setAresTimerTaskConsumer(aresTimerTaskConsumer);
        aresTimerTask.setTimeout(timeout);
        //log.info("--add  timer task ={}  timer task now count ={}", timerTask, HASHED_WHEEL_TIMER.pendingTimeouts());
        return aresTimerTask;
    }
}
