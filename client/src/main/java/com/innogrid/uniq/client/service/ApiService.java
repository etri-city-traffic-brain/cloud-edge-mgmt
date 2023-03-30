package com.innogrid.uniq.client.service;

import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.MeterServerAccumulateInfo;
import com.innogrid.uniq.core.model.MeterServerInfo;
import com.innogrid.uniq.core.model.ProjectInfo;

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

}
