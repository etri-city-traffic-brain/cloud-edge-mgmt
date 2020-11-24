package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.3.18
 * @brief 클라우드별 API 접속 정보를 관리 하는 서비스
 */
public interface CredentialService {

    /**
     * @param type Credential cloud type 정보
     * @return
     * @author wss
     * @date 2019.3.27
     * @brief Credential List
     */
    public List<CredentialInfo> getCredentialsFromMemory(String type);

    /**
     * @param credentialId Credential cloud id 정보
     * @return
     * @author wss
     * @date 2019.3.28
     * @brief Credential List
     */
    public CredentialInfo getCredentialsFromMemoryById(String credentialId);

    /**
     * @return Credential List
     * @author wss
     * @date 2019.3.27
     * @brief Credential List
     */
    public List<CredentialInfo> getCredentialsFromMemory();

    /**
     * @author wss
     * @date 2019.3.27
     * @brief Credential List update
     */
    public void updateCredentialsFromMemory();

    /**
     * @author kkm
     * @date 2019.3.18
     * @param params Credential 목록 정보 조회에 대한 파라미터
     * @brief Credential(API 접속 정보) 조회
     */
    List<CredentialInfo> getCredentials(Map<String, Object> params);

    /**
     * @param params Credential 개수 정보 조회에 대한 파라미터
     * @return int Credential 개수
     * @author kkm
     * @date 2019.3.18
     * @brief Credential 개수 조회
     */
    int getTotal(Map<String, Object> params);

    /**
     * @param params Credential 아이디
     * @return CredentialInfo 크리덴셜 정보
     * @author kkm
     * @date 2019.3.22
     * @brief Credential 아이디에 대한 CredentialI 조회
     */
    CredentialInfo getCredentialInfo(Map<String, Object> params);

    /**
     * @param info Credential 생성 정보
     * @return CredentialInfo 크리덴셜 정보
     * @author kkm
     * @date 2019.3.22
     * @brief Credential 수정
     */
    CredentialInfo updateCredential(CredentialInfo info, UserInfo reqUserInfo);

    /**
     * @param info Credential 수정 정보
     * @return CredentialInfo 크리덴셜 정보
     * @author kkm
     * @date 2019.3.22
     * @brief Credential 생성
     */
    CredentialInfo createCredential(CredentialInfo info, UserInfo reqUserInfo);

    /**
     * @param info Credential 삭제 정보
     * @return
     * @author kkm
     * @date 2019.3.22
     * @brief Credential 삭제
     */
    void deleteCredential(CredentialInfo info, UserInfo reqUserInfo);

    /**
     * @param info API로 Credential 생성
     * @return
     * @author ksg
     * @date 2020.6.11
     * @brief Credential 생성
     */

    public CredentialInfo createCredentialApi(CredentialInfo info);

}
