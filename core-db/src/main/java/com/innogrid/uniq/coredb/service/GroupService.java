package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.GroupInfo;
import com.innogrid.uniq.core.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.01
 * @brief Group 관리 서비스
 */
public interface GroupService {
    /**
     * @author wss
     * @date 2019.4.01
     * @param params Group 목록 정보 조회에 대한 파라미터
     * @brief Group 조회
     */
    List<GroupInfo> getGroups(Map<String, Object> params);

    /**
     * @param params Group 개수 정보 조회에 대한 파라미터
     * @return int Group 개수
     * @author wss
     * @date 2019.4.01
     * @brief Group 개수 조회
     */
    int getTotal(Map<String, Object> params);

    /**
     * @param params Group 아이디
     * @return Group 정보
     * @author wss
     * @date 2019.4.01
     * @brief Group 아이디에 대한 Group 정보 조회
     */
    GroupInfo getGroupInfo(Map<String, Object> params);

    /**
     * @param info Group 수정 정보
     * @param reqUserInfo 요청 유저 정보
     * @return 수정된 Group 정보
     * @author wss
     * @date 2019.4.01
     * @brief Group 수정
     */
    GroupInfo updateGroup(GroupInfo info, UserInfo reqUserInfo);

    /**
     * @param info Group 생성 정보
     * @param reqUserInfo 요청 유저 정보
     * @return 생성된 Group 정보
     * @author wss
     * @date 2019.4.01
     * @brief Group 생성
     */
    GroupInfo createGroup(GroupInfo info, UserInfo reqUserInfo);

    /**
     * @param info Group 삭제 정보
     * @param reqUserInfo 요청 유저 정보
     * @return
     * @author wss
     * @date 2019.4.01
     * @brief Group 삭제
     */
    void deleteGroup(GroupInfo info, UserInfo reqUserInfo);
}
