package com.innogrid.uniq.core.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ksg
 * @date 2020.07.02.
 * @brief API create credential data
 */
@Data
public class CredentialInfo2 implements Serializable {
    private static final long serialVersionUID = 1641101709095981091L;
    private String name;
    private String cspType;
    private String region;
    private String domain;
    private String url;
    private String tenantId;
    private String accessId;
    private String accessToken;
    private String projectId;
    private String subscriptionId;
    private List<ProjectInfo> projects;

    public CredentialInfo2() {}
}
