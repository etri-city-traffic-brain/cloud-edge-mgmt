package com.innogrid.uniq.client.exception;

import org.springframework.core.NestedRuntimeException;

public class ApiFailedException extends NestedRuntimeException {

    public ApiFailedException(String msg) {
        super(msg);
    }

}
