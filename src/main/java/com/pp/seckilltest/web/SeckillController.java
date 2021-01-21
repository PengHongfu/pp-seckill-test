package com.pp.seckilltest.web;

import com.pp.seckilltest.entity.Result;
import com.pp.seckilltest.entity.SuccessKilled;
import com.pp.seckilltest.queue.delay.jvm.SeckillQueue;
import com.pp.seckilltest.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;

/**
 * @author phf 2021/1/12 17:32
 */
@Slf4j
@Api(tags = "单应用并发测试")
@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @ApiOperation(value = "并发购买一(超卖)")
    @PostMapping("/start")
    public Result start1(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = () -> {
                Result result = seckillService.startSeckilNormal(seckillId, userId);
                log.info("用户:{} {}", userId, result.get("msg"));
                latch.countDown();
            };
            asyncServiceExecutor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            log.info("一共秒杀出{}件商品", seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }


    @ApiOperation(value = "并发购买二(加锁 可能超卖) ")
    @PostMapping("/startLock")
    public Result start2(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = () -> {
                Result result = seckillService.startSeckilLock(seckillId, userId);
                log.info("用户:{} {}", userId, result.get("msg"));
                latch.countDown();
            };
            asyncServiceExecutor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            log.info("一共秒杀出{}件商品", seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value = "并发购买三(AOP程序锁)")
    @PostMapping("/startAopLock")
    public Result start3(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = () -> {
                Result result = seckillService.startAopLock(seckillId, userId);
                log.info("用户:{} {}", userId, result.get("msg"));
                latch.countDown();
            };
            asyncServiceExecutor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            log.info("一共秒杀出{}件商品", seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value = "秒杀四(数据库悲观锁)")
    @PostMapping("/startPessimisticLock")
    public Result start4(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        int skillNum = 1000;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = () -> {
                Result result = seckillService.selectSeckillNumberPessimisticLock(seckillId, userId);
                log.info("用户:{} {}", userId, result.get("msg"));
                latch.countDown();
            };
            asyncServiceExecutor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            log.info("一共秒杀出{}件商品", seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @ApiOperation(value = "秒杀五(数据库乐观锁)")
    @PostMapping("/startSeckillOptimisticLock")
    public Result start5(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        int skillNum = 120;
        final CountDownLatch latch = new CountDownLatch(skillNum);
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = () -> {
                //这里使用的乐观锁 如果配置的抢购人数比较少、比如120:100(人数:商品) 会出现少买的情况
                Result result = seckillService.startSeckillOptimisticLock(seckillId, userId);
                log.info("用户:{} {}", userId, result.get("msg"));
                latch.countDown();
            };
            asyncServiceExecutor.execute(task);
        }
        try {
            latch.await();
            Long seckillCount = seckillService.getSeckillCount(seckillId);
            log.info("一共秒杀出{}件商品", seckillCount);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }


    @ApiOperation(value = "秒杀六 (进程内队列)")
    @PostMapping("/startQueue")
    public Result start6(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        int skillNum = 1000;
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = () -> {
                SuccessKilled kill = new SuccessKilled();
                kill.setSeckillId(seckillId);
                kill.setUserId(userId);
                //虽然进入了队列，但是不一定能秒杀成功 进队列出队有间隙
                Boolean flag = SeckillQueue.getSkillQueue().produce(kill);
            };
            asyncServiceExecutor.execute(task);
        }

        while (true) {
            if (SeckillQueue.getSkillQueue().size() == 0) {
                Long seckillCount = seckillService.getSeckillCount(seckillId);
                log.info("一共秒杀出{}件商品", seckillCount);
                break;
            }
        }
        return Result.ok();
    }

    @ApiOperation(value = "秒杀七(Disruptor队列)")
    @PostMapping("/startDisruptorQueue")
    public Result start7(long seckillId) {
        seckillService.deleteSeckill(seckillId);
        int skillNum = 1000;
        for (int i = 0; i < skillNum; i++) {
            final long userId = i;
            Runnable task = () -> {
                SuccessKilled kill = new SuccessKilled();
                kill.setSeckillId(seckillId);
                kill.setUserId(userId);
                //虽然进入了队列，但是不一定能秒杀成功 进队列出队有间隙
                Boolean flag = SeckillQueue.getSkillQueue().produce(kill);
            };
            asyncServiceExecutor.execute(task);
        }

        while (true) {
            if (SeckillQueue.getSkillQueue().size() == 0) {
                Long seckillCount = seckillService.getSeckillCount(seckillId);
                log.info("一共秒杀出{}件商品", seckillCount);
                break;
            }
        }
        return Result.ok();
    }
}
