package com.pp.seckilltest.config.repeat;

import com.pp.seckilltest.aop.RepeatLock;
import com.pp.seckilltest.config.exception.RepeatException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.lang.reflect.Method;

/**
 * redis 方案
 *
 * @author Levin
 * @since 2018/6/12 0012
 */
@Aspect
@Configuration
public class LockMethodInterceptor {


    @Autowired
    private StringRedisTemplate lockRedisTemplate;

    @Autowired
    private LockKeyGenerator cacheKeyGenerator;


    @Around("execution(public * *(..)) && @annotation(com.pp.seckilltest.aop.RepeatLock)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RepeatLock lock = method.getAnnotation(RepeatLock.class);

        final String lockKey = cacheKeyGenerator.getLockKey(pjp);
        try {
            // 采用原生 API 来实现分布式锁 原子执行设值并且设置过期时间
            Boolean success = lockRedisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.set(lockKey.getBytes(), new byte[0], Expiration.from(lock.expire(), lock.timeUnit()),
                            RedisStringCommands.SetOption.SET_IF_ABSENT));
            if (success != null && !success) {
                throw new RepeatException("请勿重复请求");
            }
            try {
                return pjp.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException("系统异常");
            }
        } finally {
            // TODO 如果演示的话需要注释该代码;实际应该放开 如果不删除,也可以等key自然失效
            //lockRedisTemplate.delete(lockKey);
        }
    }
}