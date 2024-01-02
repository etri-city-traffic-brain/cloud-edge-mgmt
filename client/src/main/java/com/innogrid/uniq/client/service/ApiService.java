package com.innogrid.uniq.client.service;

import com.innogrid.uniq.core.model.*;

import java.util.List;

/**
 * @author wss
 * @date 2019.7.05
 * @brief Credential 관련 API 서비스
 */
public interface ApiService {

    boolean getCredentialsCheck(List<CredentialInfo> list, String type);

    boolean getCredentialsNameCheck(List<CredentialInfo> list, String name);

    String getCloudType(CredentialInfo createData);

    List<CredentialInfo> getCredentialsProject(List<CredentialInfo> list, String token);

    List<CredentialInfo> getCredentialsInfo(List<CredentialInfo> list);

    boolean validateCredential(CredentialInfo info, String token);

    List<ProjectInfo> getGroupProject(List<ProjectInfo> list, String token);

    List<MeterServerInfo> getMeterServers(String cloudId, String serverId, String token);

    List<MeterServerAccumulateInfo> getMeterServerAccumulates(String cloudId, String token);

    List<MeterServerAccumulateBillingInfo> getMeterServerAccumulatesbilling(String cloudId, String token);

}
