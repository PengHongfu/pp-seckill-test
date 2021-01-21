package com.pp.seckilltest.distributedlock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zookeeper 分布式锁
 */
@Configuration
public class ZkLockUtil {

    @Value("${zookeeper.url}")
    private String address;

    @Bean
    public CuratorFramework getCuratorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(address, retryPolicy);
        client.start();
        return client;
    }
}  
