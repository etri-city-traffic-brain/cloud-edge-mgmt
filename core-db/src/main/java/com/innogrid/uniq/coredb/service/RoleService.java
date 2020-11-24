package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.RoleInfo;
import com.innogrid.uniq.core.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.03
 * @brief Role 관리 서비스
 */
public interface RoleService {
    /**
     * @author wss
     * @date 2019.4.03
     * @param params Role 목록 정보 조회에 대한 파라미터
     * @brief Role 조회
     */
    List<RoleInfo> getRoles(Map<String, Object> params);

    /**
     * @param params Role 개수 정보 조회에 대한 파라미터
     * @return int Role 개수
     * @author wss
     * @date 2019.4.03
     * @brief Role 개수 조회
     */
    int getTotal(Map<String, Object> params);

    /**
     * @param params Role 아이디
     * @return Role 정보
     * @author wss
     * @date 2019.4.03
     * @brief Role 아이디에 대한 Role 정보 조회
     */
    RoleInfo getRoleInfo(Map<String, Object> params);

    /**
     * @param info Role 수정 정보
     * @param reqUserInfo 요청 유저 정보
     * @return 수정된 Role 정보
     * @author wss
     * @date 2019.4.03
     * @brief Role 수정
     */
    RoleInfo updateRole(RoleInfo info, UserInfo reqUserInfo);

    /**
     * @param info Role 생성 정보
     * @param reqUserInfo 요청 유저 정보
     * @return 생성된 Role 정보
     * @author wss
     * @date 2019.4.03
     * @brief Role 생성
     */
    RoleInfo createRole(RoleInfo info, UserInfo reqUserInfo);

    /**
     * @param info Role 삭제 정보
     * @param reqUserInfo 요청 유저 정보
     * @return
     * @author wss
     * @date 2019.4.03
     * @brief Role 삭제
     */
    void deleteRole(RoleInfo info, UserInfo reqUserInfo);

    /**
     * @param info Role 삭제 정보
     * @param reqUserInfo 요청 유저 정보
     * @return
     * @author wss
     * @date 2019.4.08
     * @brief User의 Role 삭제
     */
    void deleteRoleUser(RoleInfo info, UserInfo reqUserInfo);

    /**
     * @param infos 추가할 User List 정보
     * @param roleId 추가될 role ID
     * @param reqUserInfo 요청 유저 정보
     * @return roleId 에 대한 User List
     * @author wss
     * @date 2019.4.12
     * @brief role에 여러 사용자 추가
     */
    List<UserInfo> createRoleToUsers(String roleId, List<UserInfo> infos, UserInfo reqUserInfo);

    /**
     * @param infos 추가할 Role List 정보
     * @param userId 추가될 User ID
     * @param reqUserInfo 요청 유저 정보
     * @return userId 에 대한 Role List
     * @author wss
     * @date 2019.4.12
     * @brief User의 Role List 추가
     */
    List<RoleInfo> createRolesToUser(List<RoleInfo> infos, String userId, UserInfo reqUserInfo);
}
