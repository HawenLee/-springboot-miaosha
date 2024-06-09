package com.hawen.miaosha.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    public static final String MIAOSHA_QUEUE = "miaosha.queue";

    // 配置Queue，对应于消息队列
    @Bean
    public Queue queue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }
}
