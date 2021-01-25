package com.pp.seckilltest.config.exception;

public class RepeatException extends RuntimeException {

    public RepeatException() {
        super();
    }

    public RepeatException(String message) {
        super(message);

    }

    public RepeatException(String message, Throwable cause) {

        super(message, cause);
    }

    public RepeatException(Throwable cause) {

        super(cause);
    }
}
