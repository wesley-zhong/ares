package com.ares.dal.rocketmq;


import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RocketMqEventProcess {
    private static final Map<String, List<RocketMqMsgEventProcess>> eventProcessMap = new HashMap<String, List<RocketMqMsgEventProcess>>();

    public void register(String topic, RocketMqMsgEventProcess eventProcess) {
        List<RocketMqMsgEventProcess> msgEventProcessList = eventProcessMap.computeIfAbsent(topic, k -> new ArrayList<>());
        msgEventProcessList.add(eventProcess);
    }

    public void onMsg(String topic, byte[] msg) {
        List<RocketMqMsgEventProcess> msgEventProcessList = eventProcessMap.get(topic);
        if (CollectionUtils.isEmpty(msgEventProcessList))
            return;
        for (RocketMqMsgEventProcess msgEventProcess : msgEventProcessList) {
            msgEventProcess.onMsgEvent(msg);
        }
    }
}
