package com.pp.seckilltest.queue.delay.jvm;

import com.pp.seckilltest.entity.Result;
import com.pp.seckilltest.entity.SuccessKilled;
import com.pp.seckilltest.enums.SeckillStatEnum;
import com.pp.seckilltest.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 消费秒杀队列
 */
@Slf4j
@Component
public class TaskRunner implements ApplicationRunner {

    @Autowired
    private SeckillService seckillService;
    @Autowired
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run(ApplicationArguments var) {

        Runnable task = () -> {
            log.info("提醒队列启动成功");
            while (true) {
                try {
                    //进程内队列
                    SuccessKilled kill = SeckillQueue.getSkillQueue().consume();
                    if (kill != null) {
                        Result result =
                                seckillService.startAopLock(kill.getSeckillId(), kill.getUserId());
                        if (result.equals(Result.ok(SeckillStatEnum.SUCCESS))) {
                            log.info("用户:{} {}", kill.getUserId(), "秒杀成功");
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        };
        asyncServiceExecutor.execute(task);
    }
}