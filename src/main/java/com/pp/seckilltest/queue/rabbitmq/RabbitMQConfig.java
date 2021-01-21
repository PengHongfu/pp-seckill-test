package com.pp.seckilltest.queue.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author phf 2021/1/21 14:50
 */
@EnableRabbit
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue queue() {
        return new Queue("test_queue");
    }
}
