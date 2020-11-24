package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.ProjectInfo;
import com.innogrid.uniq.core.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.05
 * @brief Project 관리 서비스
 */
public interface ProjectService {
    /**
     * @author wss
     * @date 2019.4.05
     * @param params Project 목록 정보 조회에 대한 파라미터
     * @brief Project 조회
     */
    List<ProjectInfo> getProjects(Map<String, Object> params);

    /**
     * @param params Project 개수 정보 조회에 대한 파라미터
     * @return int Project 개수
     * @author wss
     * @date 2019.4.05
     * @brief Project 개수 조회
     */
    int getTotal(Map<String, Object> params);

    /**
     * @param params Project 아이디
     * @return Project 정보
     * @author wss
     * @date 2019.4.05
     * @brief Project 아이디에 대한 Project 정보 조회
     */
    ProjectInfo getProjectInfo(Map<String, Object> params);

    /**
     * @param info Project 생성 정보
     * @param reqUserInfo 요청 유저 정보
     * @return 생성된 Project 정보
     * @author wss
     * @date 2019.4.05
     * @brief Project 생성
     */
    ProjectInfo createProject(ProjectInfo info, UserInfo reqUserInfo);

    /**
     * @param info Project 삭제 정보
     * @param reqUserInfo 요청 유저 정보
     * @return
     * @author wss
     * @date 2019.4.05
     * @brief Project 삭제
     */
    void deleteProject(ProjectInfo info, UserInfo reqUserInfo);

    /**
     * @param infos Project List 정보
     * @param groupId Group ID
     * @param reqUserInfo 요청 유저 정보
     * @return 생성 및 수정된 Project 리스트
     * @author wss
     * @date 2019.4.09
     * @brief Project 일괄 추가, 수정, 삭제
     */
    List<ProjectInfo> syncProject(List<ProjectInfo> infos, String groupId, UserInfo reqUserInfo);
}
