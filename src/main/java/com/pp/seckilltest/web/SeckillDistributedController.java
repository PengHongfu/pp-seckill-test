package com.pp.seckilltest.web;

import com.pp.seckilltest.entity.Result;
import com.pp.seckilltest.service.SeckillDistributedService;
import com.pp.seckilltest.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
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

    @GetMapping("/test")
    public Result test() {
        return Result.ok();
    }

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
        seckillService.deleteSeckill(seckillId);
        Result result = seckillDistributedService.startSeckilRedisLock(seckillId, userId);
        log.info("用户:{} {}", userId, result.get("msg"));
        return Result.ok();
    }
}
