package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.02
 * @brief User 관리 서비스
 */
public interface UserService {
    /**
     * @author wss
     * @date 2019.4.02
     * @param params User 목록 정보 조회에 대한 파라미터
     * @brief User 조회
     */
    public List<UserInfo> getUserInfos(Map<String, Object> params);

    /**
     * @param params User 개수 정보 조회에 대한 파라미터
     * @return int User 개수
     * @author wss
     * @date 2019.4.02
     * @brief User 개수 조회
     */
    public int getTotal(Map<String, Object> params);

    /**
     * @param info User 수정 정보
     * @param reqUserInfo 요청 유저 정보
     * @return 수정된 User 정보
     * @author wss
     * @date 2019.4.02
     * @brief User 수정
     */
    public UserInfo updateUser(UserInfo info, UserInfo reqUserInfo);

    /**
     * @param info User 생성 정보
     * @param reqUserInfo 요청 유저 정보
     * @return 생성된 User 정보
     * @author wss
     * @date 2019.4.02
     * @brief User 생성
     */
    public UserInfo createUser(UserInfo info, UserInfo reqUserInfo);

    /**
     * @param info User 삭제 정보
     * @param reqUserInfo 요청 유저 정보
     * @author wss
     * @date 2019.4.02
     * @brief User 삭제
     */
    public void deleteUser(UserInfo info, UserInfo reqUserInfo);

    /**
     * @param userId User ID 정보
     * @param password
     * @return User 존재 여부
     * @author wss
     * @date 2019.4.02
     * @brief User 존재 조회
     */
    public int getUserAuthentication(String userId, String password);

    /**
     * @param params User 정보
     * @return 조회된 사용자
     * @author wss
     * @date 2019.4.02
     * @brief 단일 User 조회
     */
    public UserInfo getUserInfo(Map<String, Object> params);

    /**
     * @param infos User List 정보
     * @param groupId Group ID
     * @param reqUser 요청 유저 정보
     * @return 조회된 사용자
     * @author wss
     * @date 2019.4.02
     * @brief User List 의 그룹 지정
     */
    public List<UserInfo> createGroupUsers(List<UserInfo> infos, String groupId, UserInfo reqUser);
}
