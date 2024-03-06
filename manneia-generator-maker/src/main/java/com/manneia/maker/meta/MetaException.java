package com.manneia.maker.meta;

/**
 * 元信息异常
 *
 * @author lkx
 */
public class MetaException extends RuntimeException{

    /**
     * 异常处理
     *
     * @param message 异常信息
     */
    public MetaException(String message) {
        super(message);
    }

    /**
     * 异常处理
     *
     * @param message 异常信息
     * @param cause 异常原因
     */
    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
