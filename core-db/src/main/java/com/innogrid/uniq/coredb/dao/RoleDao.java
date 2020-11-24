package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.RoleInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.03
 * @brief
 */
public interface RoleDao {
    List<RoleInfo> getRoles(Map<String, Object> params);

    int getTotal(Map<String, Object> params);

    RoleInfo getRoleInfo(Map<String, Object> params);

    int updateRole(RoleInfo info);

    int createRole(RoleInfo info);

    int deleteRole(RoleInfo info);


    int deleteRoleUser(RoleInfo info);

    int createRoleUser(RoleInfo info);
}

