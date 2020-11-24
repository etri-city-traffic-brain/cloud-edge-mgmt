package com.innogrid.uniq.core.exception;

import lombok.Data;

@Data
public class ApiException extends RuntimeException {

    private ErrorCode errorCode;
    private Object target;

    public ApiException(ErrorCode errorCode, String message, Object target) {
        super(message);
        this.errorCode = errorCode;
        this.target = target;
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}