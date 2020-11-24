package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.model.ActionInfo;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.6.22
 * @brief Action 서비스
 */
public interface ActionService {


    /**
     * @author kkm
     * @date 2019.6.22
     * @param params Action 목록 정보 조회에 대한 파라미터
     * @brief Action List 조회
     * @return Action 목록
     */
    public List<ActionInfo> actions(Map<String, Object> params);

    /**
     * @author wss
     * @date 2020.01.06
     * @param params action 목록 조회 파라미터
     * @brief action total
     * @return action 목록 수
     */

    public int getActionsTotal(Map<String, Object> params);


    /**
     * @author kkm
     * @date 2019.6.22
     * @param params Action 목록 정보 조회에 대한 파라미터
     * @brief Action 조회
     */
    public ActionInfo action(Map<String, Object> params);

    /**
     * @param info Action 수정 정보
     * @return 수정된 Action 정보
     * @author kkm
     * @date 2019.6.22
     * @brief Action 수정
     */
    public int updateAction(ActionInfo info);

    /**
     * @param info Action 생성 정보
     * @return 생성된 Action 정보
     * @author kkm
     * @date 2019.6.22
     * @brief Action 생성
     */
    public int createAction(ActionInfo info);

    /**
     * @param id Action 개수 정보 조회에 대한 파라미터
     * @return int Action ID 개수
     * @author kkm
     * @date 2019.6.22
     * @brief Action ID 개수 조회
     */
    public int getIDCount(String id);

    String initAction(String groupId, String userId, String content, String targetId, String targetName, Constants.ACTION_CODE actionCode, Constants.HISTORY_TYPE type);

    void setActionResult(String actionId, Constants.ACTION_RESULT opResult);

    void setActionResult(String actionId, Constants.ACTION_RESULT opResult, String resultDetail);
}
