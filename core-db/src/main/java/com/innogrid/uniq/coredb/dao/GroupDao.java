package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.GroupInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.01
 * @brief
 */
public interface GroupDao {
    List<GroupInfo> getGroups(Map<String, Object> params);

    int getTotal(Map<String, Object> params);

    GroupInfo getGroupInfo(Map<String, Object> params);

    int updateGroup(GroupInfo info);

    int createGroup(GroupInfo info);

    int deleteGroup(GroupInfo info);
}

