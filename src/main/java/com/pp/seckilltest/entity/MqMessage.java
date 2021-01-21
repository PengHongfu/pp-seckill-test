package com.pp.seckilltest.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(chain = true)
@Data
public class MqMessage implements Serializable {

    private long seckillId;
    private int userId;
}
