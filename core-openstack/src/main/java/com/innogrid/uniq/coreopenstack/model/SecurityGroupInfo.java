package com.innogrid.uniq.coreopenstack.model;

import lombok.Data;
import org.openstack4j.model.network.SecurityGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wss
 * @date 2019.4.15
 * @brief 오픈스택 SecurityGroup 모델
 */
@Data
public class SecurityGroupInfo implements Serializable {

    private static final long serialVersionUID = 3664705652851702873L;
    private String id;
    private String name;
    private String tenantId;
    private String description;
    private List<SecurityGroupRuleInfo> rules;

    public SecurityGroupInfo() {

    }

    public SecurityGroupInfo(SecurityGroup info) {
        this.id = info.getId();
        this.name = info.getName();
        if(this.name == null || this.name.equals("")) {
            this.name = this.id;
        }
        this.description = info.getDescription();
        this.tenantId = info.getTenantId();

        List<SecurityGroupRuleInfo> securityGroupRuleInfos = new ArrayList<>();

        for(int i=0; i<info.getRules().size(); i++) {
            SecurityGroupRuleInfo securityGroupRuleInfo = new SecurityGroupRuleInfo(info.getRules().get(i));
            securityGroupRuleInfos.add(securityGroupRuleInfo);
        }

        this.rules = securityGroupRuleInfos;
    }
}
