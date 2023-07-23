package com.ares.dal.rocketmq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RocketMqSender {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.topic.tag:0}")
    private String topicTag;

    public String topicRebuild(String topic) {
        if (topicTag == null || topicTag.isEmpty()) {
            return topic;
        }
        return topic + ":" + topicTag;
    }

    public boolean synSend(String topic, Object body, String tag, String hashKey) {
        topic = topicRebuild(topic);
        SendResult sendResult = rocketMQTemplate.syncSendOrderly(topic + ":" + tag, MessageBuilder.withPayload(body).build(), hashKey);
        log.info("####### send topic = {} msg = {} result = {} message-id = {}", topic + ":" + tag, body, sendResult.getSendStatus(), sendResult.getMsgId());
        return sendResult.getSendStatus() == SendStatus.SEND_OK;
    }

    public boolean synSend(String topic, Object body, String hashKey) {
        topic = topicRebuild(topic);
        SendResult sendResult = rocketMQTemplate.syncSendOrderly(topic, MessageBuilder.withPayload(body).build(), hashKey);
        log.info("####### send topic = {} msg = {} result = {} message-id = {}", topic, body, sendResult.getSendStatus(), sendResult.getMsgId());
        return sendResult.getSendStatus() == SendStatus.SEND_OK;
    }

    public void asyncSend(String topic, Object body, String tag, String hashKey) {
        topic = topicRebuild(topic);
        String finalTopic = topic;
        rocketMQTemplate.asyncSendOrderly(topic + ":" + tag, MessageBuilder.withPayload(body).build(), hashKey, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("####### topic = {} msg = {} send success {}", finalTopic, JSON.toJSONString(body), sendResult.getSendStatus());
            }

            @Override
            public void onException(Throwable e) {
                log.info("####### topic = {} msg = {} send error", finalTopic, JSON.toJSONString(body), e);
            }
        });
    }

    public void asyncSend(String topic, Object body, String hashKey) {
        topic = topicRebuild(topic);
        String finalTopic = topic;
        rocketMQTemplate.asyncSendOrderly(topic, MessageBuilder.withPayload(body).build(), hashKey, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("####### topic = {} msg = {} send success {}", finalTopic, JSON.toJSONString(body), sendResult.getSendStatus());
            }

            @Override
            public void onException(Throwable e) {
                log.info("####### topic = {} msg = {} send error", finalTopic, JSON.toJSONString(body), e);
            }
        });
    }

    public void asynSend(String topic, String hashKey, Object body) {
        topic = topicRebuild(topic);
        rocketMQTemplate.asyncSendOrderly(topic, MessageBuilder.withPayload(body).build(), hashKey, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("####### msg = {} send success {}", JSON.toJSONString(body), sendResult.getSendStatus());
            }

            @Override
            public void onException(Throwable e) {
                log.info("####### msg = {} send error", JSON.toJSONString(body), e);
            }
        });
    }

    public boolean send(String topic, String hashKey, Object body) {
        topic = topicRebuild(topic);
        String msgBody = JSON.toJSONString(body);
        SendResult sendResult = rocketMQTemplate.syncSendOrderly(topic, MessageBuilder.withPayload(msgBody).build(), hashKey);
        log.info("####### send topic = {} msg = {} result = {} message-id = {}", topic, msgBody, sendResult.getSendStatus(), sendResult.getMsgId());
        return sendResult.getSendStatus() == SendStatus.SEND_OK;
    }

}
