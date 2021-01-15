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

    @Bean(name = "asyncServiceExecutor")
    public ThreadPoolTaskExecutor asyncServiceExecutor() {
        //返回可用处理器的虚拟机的最大数量不小于1
        int cpu = Runtime.getRuntime().availableProcessors();
        log.info("start asyncServiceExecutor cpu : {}", cpu);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置核心线程数
        executor.setCorePoolSize(cpu);
        //设置最大线程数
        executor.setMaxPoolSize(cpu + 1);
        //设置队列大小
        executor.setQueueCapacity(50);
        //配置线程池的前缀
        executor.setThreadNamePrefix("secretary-");
        //用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(60);
        // 使用预定义的异常处理类
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //进行加载
        executor.initialize();
        return executor;
    }
}
