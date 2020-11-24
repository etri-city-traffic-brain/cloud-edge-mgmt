package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.network.HostRoute;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.4.12
 * @brief 오픈스택 Subnet 모델
 */
@Data
public class HostRouteInfo implements Serializable {

    private static final long serialVersionUID = -2253011978092719339L;
    private String destination;
    private String nexthop;

    public HostRouteInfo() {

    }

    public HostRouteInfo(HostRoute info) {
        this.destination = info.getDestination();
        this.nexthop = info.getNexthop();
    }
}
