package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.4.22
 * @brief 오픈스택 Resource 모델
 */
@Data
public class ResourceInfo implements Serializable {

    private static final long serialVersionUID = 3440256653567972634L;
    private int running;
    private int stop;
    private int etc;
    private int images;
    private int flavor;
    private int keyPairs;
    private int volumes;
    private int backups;
    private int snapshots;
    private int networks;
    private int routers;
    private int securityGroups;
    private int floatingIps;
    private int projects;
    private int hypervisorVcpus;
    private int hypervisorVcpusUsed;
    private int hypervisorMemory;
    private int hypervisorMemoryUsed;
    private int hypervisorDisk;
    private int hypervisorDiskUsed;

    public int getServers () {
        return running + stop + etc;
    }
}
