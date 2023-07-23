package com.ares.dal.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}",
        consumerGroup = "${rocketmq.consumer.group}" + "${mq.topic.tag:0}",
        consumeMode = ConsumeMode.ORDERLY,
        selectorExpression = "${mq.topic.tag:0}")
public class RocketMqConsumerService implements RocketMQListener<MessageExt>/*, InitializingBean*/ {

    @Autowired
    private RocketMqEventProcess eventProcess;

    @Override
    public void onMessage(MessageExt ctx) {
        try {
            log.info("rocketMq message topic = {}, tags = {}, msgId = {}, key = {}, queId = {} value = {}",
                    ctx.getTopic(), ctx.getTags(), ctx.getMsgId(), ctx.getKeys(), ctx.getQueueId(), new String(ctx.getBody()));
            eventProcess.onMsg(ctx.getTopic(), ctx.getBody());
        } catch (Exception e) {
            log.error(" ==error", e);
        }
    }

    @Value("${mq.topic.suffix:}")
    private String topicSuffix;

    @Value("${rocketmq.consumer.topic}")
    private String consumerTopics;

    @Value("#{rocketMqTopicCreater.consumerTopics}")
    private String consumerTopics2;

    private String getConsumerTopics() {
        String[] splitArr = consumerTopics.split(",");
        List<String> topicArr = new ArrayList<>();
        for (String s : splitArr) {
            topicArr.add(s + topicSuffix);
        }
        return String.join(",", topicArr);
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        RocketMQMessageListener annotation = RocketMqConsumerService.class.getAnnotation(RocketMQMessageListener.class);
//        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
//        // 获取私有 memberValues 属性
//        Field field = invocationHandler.getClass().getDeclaredField("memberValues");
//        field.setAccessible(true);
//        // 获取实例的属性map
//        Map<String, Object> memberValues = (Map<String, Object>)field.get(invocationHandler);
//        // 修改属性值
//        memberValues.put("topic", getConsumerTopics());
//    }
}
