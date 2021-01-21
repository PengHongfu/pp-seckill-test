package com.pp.seckilltest.service;

import com.pp.seckilltest.entity.Result;
import com.pp.seckilltest.entity.Seckill;
import com.pp.seckilltest.entity.SuccessKilled;
import com.pp.seckilltest.enums.SeckillStatEnum;
import com.pp.seckilltest.mapper.SeckillMapper;
import com.pp.seckilltest.mapper.SuccessKilledMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;

/**
 * @author phf 2021/1/14 17:29
 */
@Slf4j
@Service
public class SeckillDistributedService {
    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private SeckillMapper seckillMapper;

    @Autowired
    private SuccessKilledMapper successKilledMapper;

    @Autowired
    private CuratorFramework curatorFramework;

    /**
     * redisson 分布式锁
     * 锁会自动过期 防止宕机导致锁不释放
     */
    @Transactional(rollbackFor = Exception.class)
    public Result startSeckilRedisLock(long seckillId, long userId) {
        RLock lock = redissonClient.getLock(seckillId + "");
        try {
            lock.lock();
            Seckill seckill = seckillMapper.selectById(seckillId);
            if (null != seckill && seckill.getNumber() > 0) {
                int count = seckillMapper.decSeckillById(seckillId);
                if (count > 0) {
                    SuccessKilled killed = new SuccessKilled()
                            .setSeckillId(seckillId)
                            .setUserId(userId)
                            .setState(Short.parseShort(seckill.getNumber() + ""))
                            .setCreateTime(new Timestamp(System.currentTimeMillis()));
                    successKilledMapper.insert(killed);
                    return Result.ok(SeckillStatEnum.SUCCESS);
                }
            }
        } finally {
            lock.unlock();
        }
        return Result.error(SeckillStatEnum.END);
    }

    /**
     * zookeeper分布式锁
     */
    @Transactional(rollbackFor = Exception.class)
    public Result startSeckilZksLock(long seckillId, int userId) {
        InterProcessSemaphoreMutex balanceLock = new InterProcessSemaphoreMutex(curatorFramework, "/zktest-" + seckillId);
        try {
            // 执行加锁操作
            balanceLock.acquire();
            Seckill seckill = seckillMapper.selectById(seckillId);
            if (null != seckill && seckill.getNumber() > 0) {
                int count = seckillMapper.decSeckillById(seckillId);
                if (count > 0) {
                    SuccessKilled killed = new SuccessKilled()
                            .setSeckillId(seckillId)
                            .setUserId(userId)
                            .setState(Short.parseShort(seckill.getNumber() + ""))
                            .setCreateTime(new Timestamp(System.currentTimeMillis()));
                    successKilledMapper.insert(killed);
                    return Result.ok(SeckillStatEnum.SUCCESS);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                // 释放锁资源
                balanceLock.release();
            } catch (Exception e) {
                log.error("释放锁资源异常", e);
            }
        }
        return Result.error(SeckillStatEnum.END);
    }
}
