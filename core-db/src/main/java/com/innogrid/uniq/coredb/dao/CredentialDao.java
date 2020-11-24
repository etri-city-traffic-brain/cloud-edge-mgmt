package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.CredentialInfo;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.3.18
 * @brief
 */
public interface CredentialDao {
    List<CredentialInfo> getCredentials(Map<String, Object> params);

    int getTotal(Map<String, Object> params);

    CredentialInfo getCredentialInfo(Map<String, Object> params);

    int updateCredential(CredentialInfo info);

    int createCredential(CredentialInfo info);

    int deleteCredential(CredentialInfo info);
}

