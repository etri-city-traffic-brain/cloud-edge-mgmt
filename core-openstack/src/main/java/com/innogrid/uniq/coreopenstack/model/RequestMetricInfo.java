package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.7.10
 * @brief Metric 요청용 모델
 */
@Data
public class RequestMetricInfo implements Serializable {
    private static final long serialVersionUID = 6221306866939451227L;
    private Integer metricName;
    private String statistic;
    private Integer interval;
    private Long endDate;
    private Long startDate;
    private String id;

    public RequestMetricInfo() {
    }
}
