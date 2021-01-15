package com.pp.seckilltest.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Accessors(chain = true)
@Data
@TableName("seckill")
public class Seckill implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private long seckillId;
    private String name;
    private int number;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp createTime;
    private int version;

}
