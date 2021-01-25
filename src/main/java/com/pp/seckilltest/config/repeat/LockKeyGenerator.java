package com.pp.seckilltest.config.repeat;

import com.pp.seckilltest.aop.RepeatLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author PengHongfu 2021/1/25 14:27
 */
@Component
public class LockKeyGenerator {

    public String getLockKey(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RepeatLock lockAnnotation = method.getAnnotation(RepeatLock.class);
        final Object[] args = pjp.getArgs();
        final Parameter[] parameters = method.getParameters();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < parameters.length; i++) {
            builder.append(lockAnnotation.delimiter()).append(args[i]);
        }
        if (StringUtils.isEmpty(builder.toString())) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {

                    field.setAccessible(true);
                    builder.append(lockAnnotation.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        //后续加入号码
        String phone = "10086";
        if (!StringUtils.isEmpty(lockAnnotation.prefix())) {
            return lockAnnotation.prefix() + phone + builder.toString();
        }
        return method.getName() + phone + builder.toString();
    }
}
