package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.network.Router;

import java.io.Serializable;
import java.util.List;

/**
 * @author wss
 * @date 2019.3.25
 * @brief 오픈스택 Router 모델
 */
@Data
public class RouterInfo implements Serializable {

    private static final long serialVersionUID = 1937456454879937967L;
    private String id;
    private String name;
    private String state;
    private String networkId;
    private String networkName;
    private Boolean adminStateUp;
    private List<String> visibilityZones;
    private String projectId;
    private String projectName;

    public RouterInfo() {

    }

    public RouterInfo(Router info) {
        if(info != null) {
            this.id = info.getId();
            this.name = info.getName();
            this.state = info.getStatus().name();
            if (info.getExternalGatewayInfo() != null) this.networkId = info.getExternalGatewayInfo().getNetworkId();
            this.adminStateUp = info.isAdminStateUp();
            this.projectId = info.getTenantId();
        }
    }
}
