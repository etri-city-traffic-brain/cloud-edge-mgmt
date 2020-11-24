package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.network.Subnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wss
 * @date 2019.4.12
 * @brief 오픈스택 Subnet 모델
 */
@Data
public class SubnetInfo implements Serializable {

    private static final long serialVersionUID = 511058908511208546L;
    private String id;
    private String name;
    private String cidr;
    private int ipVersion;
    private String gateway;
    private String networkId;
    private Boolean dhcpEnabled;
    private List<String> dnsNames;
    private List<PoolInfo> allocationPools;
    private List<HostRouteInfo> hostRoutes;

    public SubnetInfo() {

    }

    public SubnetInfo(Subnet info) {
        if(info != null) {
            this.id = info.getId();
            this.name = info.getName();
            this.cidr = info.getCidr();
            this.ipVersion = info.getIpVersion().getVersion();
            this.gateway = info.getGateway();
            this.networkId = info.getNetworkId();
            this.dhcpEnabled = info.isDHCPEnabled();
            this.dnsNames = info.getDnsNames();

            List<PoolInfo> poolInfos = new ArrayList<>();

            for (int i = 0; i < info.getAllocationPools().size(); i++) {
                poolInfos.add(new PoolInfo(info.getAllocationPools().get(i)));
            }
            this.allocationPools = poolInfos;

            List<HostRouteInfo> hostRouteInfos = new ArrayList<>();

            for (int i = 0; i < info.getHostRoutes().size(); i++) {
                hostRouteInfos.add(new HostRouteInfo(info.getHostRoutes().get(i)));
            }
            this.hostRoutes = hostRouteInfos;
        }
    }
}
