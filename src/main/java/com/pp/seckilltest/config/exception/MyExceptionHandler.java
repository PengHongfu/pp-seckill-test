package com.pp.seckilltest.config.exception;


import com.pp.seckilltest.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理
 *
 * @author by PengHongfu 2018/12/13 15:16
 */
@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Object exception(RuntimeException e) {
        log.error("出现异常", e);
        return Result.error("系统繁忙，请稍后重试！");
    }

}
