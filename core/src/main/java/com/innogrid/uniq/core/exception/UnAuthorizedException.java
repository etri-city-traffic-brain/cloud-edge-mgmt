package com.innogrid.uniq.core.exception;

import org.springframework.core.NestedRuntimeException;

public class UnAuthorizedException extends NestedRuntimeException {
    private String title = "";
    private String detail = "";

    public UnAuthorizedException(String msg) {
        super(msg);
        this.title = msg;
        this.detail = msg;
    }

    public UnAuthorizedException(String title, String detail) {
        super(title);
        this.title = title;
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }
}
