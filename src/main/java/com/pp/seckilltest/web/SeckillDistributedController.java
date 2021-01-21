package com.pp.seckilltest.web;

import com.pp.seckilltest.entity.Result;
import com.pp.seckilltest.service.SeckillDistributedService;
import com.pp.seckilltest.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 启动多个端口的应用 使用nginx 使用JMeter子类的工具并发测试
 *
 * @author phf 2021/1/14 17:27
 */

@Slf4j
@Api(tags = "多应用 分布式并发测试")
@RestController
@RequestMapping("/seckillDistributed")
public class SeckillDistributedController {

    @Autowired
    private SeckillDistributedService seckillDistributedService;
    @Autowired
    private SeckillService seckillService;

    @Autowired
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @Autowired
    private AmqpTemplate template;

    @ApiOperation(value = "秒杀结果")
    @GetMapping("/result")
    public Result result(long seckillId) {
        Long seckillCount = seckillService.getSeckillCount(seckillId);
        log.info("一共秒杀出{}件商品", seckillCount);
        return Result.ok(seckillCount);
    }

    @ApiOperation(value = "秒杀一(Rediss分布式锁)")
    @PostMapping("/startRedisLock")
    public Result start1(long seckillId, int userId) {
        Result result = seckillDistributedService.startSeckilRedisLock(seckillId, userId);
        log.info("用户:{} {}", userId, result.get("msg"));
        return Result.ok();
    }

    @ApiOperation(value = "初始化商品")
    @GetMapping("/init")
    public Result test(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        return Result.ok();
    }

    @ApiOperation(value = "秒杀二(zookeeper分布式锁)")
    @PostMapping("/startZkLock")
    public Result start2(long seckillId, int userId) {
        Result result = seckillDistributedService.startSeckilZksLock(seckillId, userId);
        log.info("用户:{} {}", userId, result.get("msg"));
        return Result.ok();
    }

    @ApiOperation(value = "秒杀三(rabbitMq消息队列)")
    @PostMapping("/startRabbitMQ")
    public Result start3(long seckillId, int userId) {
        template.convertAndSend("test_queue", seckillId + ";" + userId);
        return Result.ok();
    }
}
