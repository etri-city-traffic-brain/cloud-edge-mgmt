package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.PermissionInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.08
 * @brief
 */
public interface PermissionDao {
    List<PermissionInfo> getPermissions(Map<String, Object> params);

    List<PermissionInfo> getUserPermissions(Map<String, Object> params);

    int getTotal(Map<String, Object> params);

    PermissionInfo getPermissionInfo(Map<String, Object> params);

    int createPermission(PermissionInfo info);

    int deletePermission(PermissionInfo info);
}

