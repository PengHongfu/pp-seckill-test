package com.pp.seckilltest.delay.disruptor;

import com.lmax.disruptor.EventHandler;
import com.pp.seckilltest.config.SpringUtil;
import com.pp.seckilltest.entity.Result;
import com.pp.seckilltest.enums.SeckillStatEnum;
import com.pp.seckilltest.service.SeckillService;
import lombok.extern.slf4j.Slf4j;

/**
 * 消费者(秒杀处理器)
 * 创建者 科帮网
 */
@Slf4j
public class SeckillEventConsumer implements EventHandler<SeckillEvent> {

    private SeckillService seckillService = SpringUtil.getBean(SeckillService.class);

    @Override
    public void onEvent(SeckillEvent seckillEvent, long seq, boolean bool) {
        Result result = seckillService.startAopLock(seckillEvent.getSeckillId(), seckillEvent.getUserId());
        if (result.equals(Result.ok(SeckillStatEnum.SUCCESS))) {
            log.info("用户:{} {}", seckillEvent.getUserId(), "秒杀成功");
        }
    }
}
