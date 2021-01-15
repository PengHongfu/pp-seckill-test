package com.pp.seckilltest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.seckilltest.entity.SuccessKilled;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface SuccessKilledMapper extends BaseMapper<SuccessKilled> {

    /**
     * 删除秒杀售卖商品记录
     */
    @Delete("DELETE FROM  success_killed WHERE seckill_id=#{seckillId}")
    int deleteSuccessKilledRecordBySeckillId(long seckillId);

    @Select("SELECT count(*) FROM success_killed WHERE seckill_id=#{seckillId}")
    Long getSeckillCount(long seckillId);
}
