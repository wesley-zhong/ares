package com.ares.dal.rocketmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnProperty(value = "global.region.unload", havingValue = "false", matchIfMissing = true)
public class RocketMqTopicCreater {

    @Value("${rocketmq.consumer.topic}")
    private String consumerTopics;

    @Value("${mq.topic.suffix:}")
    private String topicSuffix;

    public String getConsumerTopics() {
        String[] splitArr = consumerTopics.split(",");
        List<String> topicArr = new ArrayList<>();
        for (String s : splitArr) {
            topicArr.add(s + topicSuffix + ":xx");
        }
        return String.join(",", topicArr);
    }

    public String topicRebuild(String topic) {
        if (topicSuffix == null || topicSuffix.isEmpty()) {
            return topic;
        }
        return topic + topicSuffix;
    }
}
