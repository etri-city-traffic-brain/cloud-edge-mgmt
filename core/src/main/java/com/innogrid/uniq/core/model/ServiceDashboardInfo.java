package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.util.JsonDateDeserializer;
import com.innogrid.uniq.core.util.JsonDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by kkm on 15. 4. 24.
 */
@Data
public class ServiceDashboardInfo implements Serializable {
    private static final long serialVersionUID = 1779366790605206372L;
    private String id;
    private String type;
    private Integer totalServer;
    private Integer runningServer;
    private Integer stoppedServer;
    private Integer etcServer;
    private Integer account;
    private Integer network;
    private Integer publicIp;
    private Integer loadbalancer;
    private Integer securityGroup;
    private long hypervisorVcpus;
    private long hypervisorVcpusUsed;
    private long hypervisorMemory;
    private long hypervisorMemoryUsed;
    private long hypervisorDisk;
    private long hypervisorDiskUsed;
    private Integer volume;
    private Integer snapshot;
    private Integer image;
    private Integer keypair;
    private Integer subnet;
    private Integer storageAccount;
    private Integer disk;
    private Integer activeDirectoryGroup;
    private Integer activeDirectoryUser;
    private Integer flavor;
    private Integer backup;
    private Integer router;
    private Integer diskCount;
    private Integer diskUsage;
    private Integer storageCount;
    private Integer storageUsage;
    private Integer databaseCount;
    private Integer databaseUsage;
    private Integer datacenter;
    private Integer cluster;
    private Integer host;
    private Integer datastore;
    private Double currentCost;
    private Double predictMonthCost;
    private Double previousCost;
    private Double previousMonthCost;

    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Timestamp lastUpdatedAt;

    public ServiceDashboardInfo(){

    }
    public Integer getResources() {
        Integer resource = 0;

        if(totalServer != null) resource += totalServer;
        if(network != null) resource += network;
        if(publicIp != null) resource += publicIp;
        if(loadbalancer != null) resource += loadbalancer;
        if(securityGroup != null) resource += securityGroup;
        if(volume != null) resource += volume;
        if(snapshot != null) resource += snapshot;
        if(image != null) resource += image;
        if(keypair != null) resource += keypair;
        if(subnet != null) resource += subnet;
        if(storageAccount != null) resource += storageAccount;
        if(disk != null) resource += disk;
        if(activeDirectoryGroup != null) resource += activeDirectoryGroup;
        if(activeDirectoryUser != null) resource += activeDirectoryUser;
        if(flavor != null) resource += flavor;
        if(backup != null) resource += backup;
        if(router != null) resource += router;
        if(datacenter != null) resource += datacenter;
        if(cluster != null) resource += cluster;
        if(host != null) resource += host;
        if(datastore != null) resource += datastore;

        return resource;
    }

    public ServiceDashboardInfo(String id){
        Timestamp time = new Timestamp(new Date().getTime());

        this.id = id;
        this.totalServer = 1;
        this.runningServer = 1;
        this.etcServer = 0;
        this.stoppedServer = 0;
        this.lastUpdatedAt = time;
    }
}
