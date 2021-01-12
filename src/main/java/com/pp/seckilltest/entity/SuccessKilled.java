package com.pp.seckilltest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Accessors(chain = true)
@Data
@TableName("success_killed")
public class SuccessKilled implements Serializable {
    private static final long serialVersionUID = 2L;

    private long seckillId;
    private long userId;
    private short state;
    private Timestamp createTime;
}
