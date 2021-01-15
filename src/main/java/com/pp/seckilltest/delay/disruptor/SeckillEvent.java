package com.pp.seckilltest.delay.disruptor;

import lombok.Data;

import java.io.Serializable;

/**
 * 事件对象（秒杀事件）
 */
@Data
public class SeckillEvent implements Serializable {
    private long seckillId;
    private long userId;
}