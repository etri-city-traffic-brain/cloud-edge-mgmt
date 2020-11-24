package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.network.NetFloatingIP;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.4.15
 * @brief 오픈스택 SecurityGroup 모델
 */
@Data
public class FloatingIpInfo implements Serializable {

    private static final long serialVersionUID = 4155421035780298423L;
    private String id;
    private String routerId;
    private String tenantId;
    private String projectName;
    private String floatingNetworkId;
    private String floatingIpAddress;
    private String fixedIpAddress;
    private String portId;
    private String status;
    private String serverName;
    private String networkName;
    private String instanceId;
    private String pool;

    public FloatingIpInfo() {

    }

    public FloatingIpInfo(NetFloatingIP info) {
        this.id = info.getId();
        this.routerId = info.getRouterId();
        this.tenantId = info.getTenantId();
        this.floatingNetworkId = info.getFloatingNetworkId();
        this.floatingIpAddress = info.getFloatingIpAddress();
        this.fixedIpAddress = info.getFixedIpAddress();
        this.portId = info.getPortId();
        this.status = info.getStatus();
    }

    public FloatingIpInfo(FloatingIP info) {
        this.instanceId = info.getInstanceId();
        this.pool = info.getPool();
        this.id = info.getId();
        this.floatingIpAddress = info.getFloatingIpAddress();
        this.fixedIpAddress = info.getFixedIpAddress();
    }
}
