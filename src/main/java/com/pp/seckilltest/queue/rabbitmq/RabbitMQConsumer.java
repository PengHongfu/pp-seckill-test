package com.pp.seckilltest.queue.rabbitmq;

import com.pp.seckilltest.entity.Result;
import com.pp.seckilltest.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author PengHongfu 2021/1/21 14:52
 */
@Slf4j
@Component
public class RabbitMQConsumer {

    @Autowired
    private SeckillService seckillService;

    @RabbitListener(queues = "test_queue")
    public void consume(String message) {
        String[] array = message.split(";");
        Result result = seckillService.startAopLock(Long.parseLong(array[0]), Long.parseLong(array[1]));
        log.info("用户:{} {}", array[1], result.get("msg"));
    }

}
