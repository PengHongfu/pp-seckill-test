package com.pp.seckilltest.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步锁 AOP
 * 创建者	张志朋
 * 创建时间	2015年6月3日
 *
 * @transaction 中  order 大小的说明
 * https://docs.spring.io/spring/docs/4.3.14.RELEASE/spring-framework-reference/htmlsingle/#transaction-declarative-annotations
 * https://docs.spring.io/spring/docs/4.3.14.RELEASE/javadoc-api/
 * order越小越是最先执行，但更重要的是最先执行的最后结束。order默认值是2147483647
 */
@Component
@Scope
@Aspect
@Order(1)
public class LockAspect {

    private static Lock lock = new ReentrantLock(true);

    @Pointcut("@annotation(com.pp.seckilltest.aop.Servicelock)")
    public void lockAspect() {
    }

    @Around("lockAspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object obj;
        lock.lock();
        try {
            obj = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return obj;
    }
}
