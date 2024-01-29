package com.ares.core.thread;


import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.utils.AresContextThreadLocal;
import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class AresEventHandler implements EventHandler<AresPacketEvent> {
    @Override
    public void onEvent(AresPacketEvent event, long sequence, boolean endOfBatch) {

        eventCall(event);
        clearEvent(event);
    }

    public void eventCall(AresPacketEvent event){
        AresMsgIdMethod method = event.getMethod();
        AresContextThreadLocal.cache(event.getAresTKcpContext());
        method.getAresServiceProxy().callMethod(method, event.getParam1(), event.getParam2());
    }


    private void clearEvent(AresPacketEvent event) {
        event.clear();
    }
}
