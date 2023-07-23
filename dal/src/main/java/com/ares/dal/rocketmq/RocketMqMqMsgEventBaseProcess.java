package com.ares.dal.rocketmq;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;


public abstract class RocketMqMqMsgEventBaseProcess<T> implements RocketMqMsgEventProcess, InitializingBean {
    @Autowired
    private RocketMqEventProcess eventProcess;

    private Class<T> objClass;
    private Type type;

    public RocketMqMqMsgEventBaseProcess(Class<T> objClass) {
        this.objClass = objClass;
    }

    public RocketMqMqMsgEventBaseProcess(Type type) {
        this.type = type;
    }


    @Override
    public void onMsgEvent(byte[] msg) {
        T obj = null;
        if (this.objClass != null) {
            obj = JSON.parseObject(msg, this.objClass);
        }
        if (obj == null && this.type != null) {
            obj = JSON.parseObject(msg, type);
        }
        this.onMsg(obj);
    }

    public abstract void onMsg(T msg);

    public abstract String getRegisterTopic();

    @Override
    public void afterPropertiesSet() throws Exception {
        eventProcess.register(this.getRegisterTopic(), this);
    }
}
