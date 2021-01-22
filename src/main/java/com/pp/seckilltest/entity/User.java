package com.pp.seckilltest.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author PengHongfu 2021/1/22 14:38
 */
@Accessors(chain = true)
@Data
@Document("user")
public class User {
    @Id
    private String id;

    private String userName;
    private String passWord;
}