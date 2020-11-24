package com.innogrid.uniq.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestInfo implements Serializable {
    private static final long serialVersionUID = 3058286392978132651L;
    private String id;
    private String action;
    private String userId;
    private Object object;
}
