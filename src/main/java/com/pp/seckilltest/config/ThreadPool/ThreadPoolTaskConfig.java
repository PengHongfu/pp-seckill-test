package com.pp.seckilltest.config.ThreadPool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池
 *
 * @author phf 2020/6/4 17:27
 */
@Slf4j
@Configuration
public class ThreadPoolTaskConfig implements AsyncConfigurer {

    @Bean
    public ThreadPoolTaskExecutor asyncServiceExecutor() {
        //返回可用处理器的虚拟机的最大数量不小于1
        int cpu = Runtime.getRuntime().availableProcessors();
        log.info("start asyncServiceExecutor cpu : {}", cpu);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置核心线程数
        executor.setCorePoolSize(cpu);
        //设置最大线程数
        executor.setMaxPoolSize(10);
        //设置队列大小
        executor.setQueueCapacity(15);
        //配置线程池的前缀
        executor.setThreadNamePrefix("phoneList-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //设置空闲时间
        executor.setKeepAliveSeconds(60);
        //进行加载
        executor.initialize();
        return executor;
    }
}
