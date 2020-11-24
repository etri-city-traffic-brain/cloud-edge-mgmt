package com.innogrid.uniq.client.exception;

import org.springframework.core.NestedRuntimeException;

public class ValidationFailException extends NestedRuntimeException {

    public ValidationFailException(String msg) {
        super(msg);
    }

}
