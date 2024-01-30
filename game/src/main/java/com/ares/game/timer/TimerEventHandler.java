package com.ares.game.timer;

import com.ares.core.thread.LogicProcessThreadPool;
import com.ares.core.timer.AresTimerTask;
import com.ares.core.timer.ScheduleService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


@Component
public class TimerEventHandler implements InitializingBean {
    private ScheduleService scheduleService;
    public void  onTimerTask(AresTimerTask aresTimerTask){
        LogicProcessThreadPool.INSTANCE.execute(aresTimerTask.getExecuteHashCode(),aresTimerTask,(timerTask)->{
            if(timerTask.isValid()){
                timerTask.getCall().apply(aresTimerTask.getExtData());
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduleService =new ScheduleService(this::onTimerTask);
    }
}
