package com.hawen.miaosha.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hawen.miaosha.redis.FkRedisUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
public class MiaoshaSender {
    private final AmqpTemplate amqpTemplate;

    public MiaoshaSender(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void sendMiaoshaMessage(MiaoshaMessage miaoshaMessage) throws JsonProcessingException {
        // 将MiaoshaMessage转换成字符串
        String msg = FkRedisUtil.beanToString(miaoshaMessage);
        // 发送消息
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }
}
