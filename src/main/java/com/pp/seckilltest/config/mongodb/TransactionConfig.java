package com.pp.seckilltest.config.mongodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * 开启mongodb事务
 *
 * @author phf 2021/1/22 14:35
 */
@Configuration
public class TransactionConfig {

    @Bean
    MongoTransactionManager transactionManager(MongoDbFactory factory) {
        return new MongoTransactionManager(factory);
    }

}
