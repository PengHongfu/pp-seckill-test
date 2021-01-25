package com.pp.seckilltest.service;

import com.pp.seckilltest.aop.Servicelock;
import com.pp.seckilltest.entity.Result;
import com.pp.seckilltest.entity.Seckill;
import com.pp.seckilltest.entity.SuccessKilled;
import com.pp.seckilltest.enums.SeckillStatEnum;
import com.pp.seckilltest.mapper.SeckillMapper;
import com.pp.seckilltest.mapper.SuccessKilledMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 单应用业务层
 *
 * @author phf 2021/1/12 17:51
 */
@Slf4j
@Service
public class SeckillService {

    @Resource
    private SeckillMapper seckillMapper;

    @Autowired
    private SuccessKilledMapper successKilledMapper;

    /**
     * 不公平锁
     */
    private Lock lock = new ReentrantLock(true);

    /**
     * 查询秒杀售卖商品
     */
    public Seckill findSeckillList(long seckillId) {
        return seckillMapper.selectById(seckillId);
    }

    /**
     * 删除秒杀售卖商品记录
     */
    public void deleteSeckill(long seckillId) {
        successKilledMapper.deleteSuccessKilledRecordBySeckillId(seckillId);
        seckillMapper.updateSeckillNumberById(seckillId);
    }

    /**
     * 查询秒杀售卖商品
     */
    public Long getSeckillCount(long seckillId) {
        return successKilledMapper.getSeckillCount(seckillId);
    }

    /**
     * 不加措施
     *
     * @param seckillId 商品id
     * @param userId    用户id
     * @return 抢购结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result startSeckilNormal(long seckillId, long userId) {
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
        return Result.error(SeckillStatEnum.END);
    }

    /**
     * 加锁
     *
     * @param seckillId 商品id
     * @param userId    用户id
     * @return 抢购结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result startSeckilLock(long seckillId, long userId) {
        /*
         会被超卖 原因: 某个事物在未提交之前，锁已经释放(事物提交是在整个方法执行完)，导致下一个事物读取到了上个事物未提交的数据，也就是传说中的脏读。
         此处给出的建议是锁上移(下个方法AOP程序锁)，也就是说要包住整个事物单元。
         */
        lock.lock();
        try {
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
            return Result.error(SeckillStatEnum.END);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 程序锁AOP
     */
    @Servicelock
    @Transactional(rollbackFor = Exception.class)
    public Result startAopLock(long seckillId, long userId) {
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
        return Result.error(SeckillStatEnum.END);
    }

    /**
     * 数据库悲观锁
     */
    @Transactional(rollbackFor = Exception.class)
    public Result selectSeckillNumberPessimisticLock(long seckillId, long userId) {
        int number = seckillMapper.selectSeckillNumberPessimisticLock(seckillId);
        if (number > 0) {
            int count = seckillMapper.decSeckillById(seckillId);
            if (count > 0) {
                SuccessKilled killed = new SuccessKilled()
                        .setSeckillId(seckillId)
                        .setUserId(userId)
                        .setState(Short.parseShort(number + ""))
                        .setCreateTime(new Timestamp(System.currentTimeMillis()));
                successKilledMapper.insert(killed);
                return Result.ok(SeckillStatEnum.SUCCESS);
            }
        }
        return Result.error(SeckillStatEnum.END);

    }

    /**
     * 数据库乐观锁
     */
    @Transactional(rollbackFor = Exception.class)
    public Result startSeckillOptimisticLock(long seckillId, long userId) {
        Seckill seckill = seckillMapper.selectById(seckillId);
        if (null != seckill && seckill.getNumber() > 0) {
            //乐观锁
            int count = seckillMapper.getSeckillByIdAndVersion(seckillId, seckill.getVersion());
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
        return Result.error(SeckillStatEnum.END);
    }
}
