/*
 * By Born
 * Copyright (c) 2017-2019. All Rights Reserved.
 */
package cn.cyejing.shuttle.common.encryption.exception;

/**
 * @author Born
 * @version : CryotoException.java,v 0.1 2020年03月28日 14:56
 */
public class CryptoException extends RuntimeException {
    public CryptoException() {
    }

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(Throwable cause) {
        super(cause);
    }

    public CryptoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
