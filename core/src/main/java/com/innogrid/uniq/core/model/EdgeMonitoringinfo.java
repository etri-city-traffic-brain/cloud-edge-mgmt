package com.innogrid.uniq.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class EdgeMonitoringinfo implements Serializable {
    private static final long serialVersionUID = 4705455919358933691L;
    private String start;
    private String stop;
    private String time;
    private String value;
    private String field;
    private String measurement;
    private String host;
}
