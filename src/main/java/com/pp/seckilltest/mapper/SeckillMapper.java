package com.pp.seckilltest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.seckilltest.entity.Seckill;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface SeckillMapper extends BaseMapper<Seckill> {

    /**
     * 更新商品数量
     */
    @Update("UPDATE seckill SET number =100 WHERE seckill_id=#{seckillId}")
    int updateSeckillNumberById(long seckillId);

    /**
     * 抢购商品 数量-1
     */
    @Update("UPDATE seckill SET number =number-1 WHERE seckill_id=#{seckillId}")
    int decSeckillById(long seckillId);

    /**
     * 查询商品数量 悲观锁
     * seckill_id为主键 只对查询行加锁
     * <p>
     * 使用select…for update会把数据给锁住，不过我们需要注意一些锁的级别，MySQL InnoDB默认Row-Level Lock，
     * 所以只有「明确」地指定主键，MySQL 才会执行Row lock (只锁住被选取的数据) ，否则MySQL 将会执行Table Lock (将整个数据表单给锁住)。
     * <p>
     * 索引和主键同理 如果查询结果有多个值,seckill_id > **  主键结果多个(不明确) 也会锁表
     */
    @Select("SELECT number FROM seckill WHERE seckill_id=#{seckillId} FOR UPDATE")
    int selectSeckillNumberPessimisticLock(long seckillId);

    /**
     * 抢购商品 数量-1
     */
    @Update("UPDATE seckill  SET number=number-1,version=version+1 WHERE seckill_id=#{seckillId} AND version = #{version}")
    int getSeckillByIdAndVersion(@Param("seckillId") long seckillId, @Param("version") int version);
}
