package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.PermissionInfo;
import com.innogrid.uniq.core.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.08
 * @brief Permission 관리 서비스
 */
public interface PermissionService {
    /**
     * @author wss
     * @date 2019.4.08
     * @param params Permission 목록 정보 조회에 대한 파라미터
     * @brief Permission 조회
     */
    List<PermissionInfo> getPermissions(Map<String, Object> params);

    /**
     * @param params Permission 개수 정보 조회에 대한 파라미터
     * @return int Permission 개수
     * @author wss
     * @date 2019.4.08
     * @brief Permission 개수 조회
     */
    int getTotal(Map<String, Object> params);

    /**
     * @param params Permission 아이디
     * @return Permission 정보
     * @author wss
     * @date 2019.4.08
     * @brief Permission 아이디에 대한 Permission 정보 조회
     */
    PermissionInfo getPermissionInfo(Map<String, Object> params);

    /**
     * @param info Permission 생성 정보
     * @param reqUserInfo 요청 유저 정보
     * @return 생성된 Permission 정보
     * @author wss
     * @date 2019.4.08
     * @brief Permission 생성
     */
    PermissionInfo createPermission(PermissionInfo info, UserInfo reqUserInfo);

    /**
     * @param info Permission 삭제 정보
     * @param reqUserInfo 요청 유저 정보
     * @return
     * @author wss
     * @date 2019.4.08
     * @brief Permission 삭제
     */
    void deletePermission(PermissionInfo info, UserInfo reqUserInfo);

    /**
     * @param infos Permission 생성 정보
     * @param roleId role ID
     * @param reqUserInfo 요청 유저 정보
     * @return RoleId에 대한 퍼미션 리스트
     * @author wss
     * @date 2019.4.12
     * @brief Role ID에 대한 Permission 추가
     */
    List<PermissionInfo> createPermissionsToRole(List<PermissionInfo> infos, String roleId, UserInfo reqUserInfo);
}
