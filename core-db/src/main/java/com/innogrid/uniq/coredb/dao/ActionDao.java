package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.ActionInfo;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.6.22
 * @brief
 */
public interface ActionDao {
    public List<ActionInfo> getActions(Map<String, Object> params);

    int getActionsTotal(Map<String, Object> params);

    public ActionInfo getAction(Map<String, Object> params);

    int createAction(ActionInfo info);

    int updateAction(ActionInfo info);

    int deleteAction(ActionInfo info);

    int getIDCount(String id);
}
