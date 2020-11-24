package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.network.SecurityGroupRule;

import java.io.Serializable;

/**
 * @author wss
 * @date 2019.4.15
 * @brief 오픈스택 SecurityGroup 모델
 */
@Data
public class SecurityGroupRuleInfo implements Serializable {

    private static final long serialVersionUID = 2460258749568955263L;
    private String id;
    private String direction;
    private String etherType;
    private Integer portRangeMax;
    private Integer portRangeMin;
    private String protocol;
    private String remoteGroupId;
    private String remoteIpPrefix;
    private String securityGroupId;
    private String tenantId;

    public SecurityGroupRuleInfo() {

    }

    public SecurityGroupRuleInfo(SecurityGroupRule info) {
        this.id = info.getId();
        this.direction = info.getDirection();
        this.etherType = info.getEtherType();
        this.portRangeMax = info.getPortRangeMax();
        this.portRangeMin = info.getPortRangeMin();
        this.protocol = info.getProtocol();
        this.remoteGroupId = info.getRemoteGroupId();
        this.remoteIpPrefix = info.getRemoteIpPrefix();
        this.securityGroupId = info.getSecurityGroupId();
        this.tenantId = info.getTenantId();
    }
}
