package com.ares.dal.rocketmq;

public interface RocketMqMsgEventProcess {
    void onMsgEvent(byte[] msg);
}
