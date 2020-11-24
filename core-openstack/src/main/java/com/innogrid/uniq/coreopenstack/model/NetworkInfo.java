package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.network.Network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wss
 * @date 2019.3.25
 * @brief 오픈스택 Network 모델
 */
@Data
public class NetworkInfo implements Serializable {

    private static final long serialVersionUID = -18128771593660073L;
    private String id;
    private String name;
    private List<SubnetInfo> neutronSubnets;
    private Boolean shared;
    private Boolean external;
    private String state;
    private Boolean adminStateUp;
    private List<String> visibilityZones;
    private String projectId;
    private String projectName;

    public NetworkInfo() {

    }

    public NetworkInfo(Network info) {
        if(info != null) {
            this.id = info.getId();
            this.name = info.getName();
            List<SubnetInfo> subnets = new ArrayList<>();
            if (info.getNeutronSubnets() != null) {
                for (int i = 0; i < info.getNeutronSubnets().size(); i++) {
                    subnets.add(new SubnetInfo(info.getNeutronSubnets().get(i)));
                }
            }
            this.neutronSubnets = subnets;
            this.shared = info.isShared();
            this.external = info.isRouterExternal();
            this.state = info.getStatus().name();
            this.adminStateUp = info.isAdminStateUp();
            this.visibilityZones = info.getAvailabilityZones();
            this.projectId = info.getTenantId();
        }
    }
}
