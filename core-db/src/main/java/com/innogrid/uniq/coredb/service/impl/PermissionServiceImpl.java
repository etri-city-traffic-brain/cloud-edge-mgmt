package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.model.PermissionInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.dao.PermissionDao;
import com.innogrid.uniq.coredb.service.ActionService;
import com.innogrid.uniq.coredb.service.PermissionService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author wss
 * @date 2019.4.08
 * @brief
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {
    private final static Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Autowired
    private PermissionDao roleDao;

    @Autowired
    private ActionService actionService;

    @Override
    public List<PermissionInfo> getPermissions(Map<String, Object> params) {
        List<PermissionInfo> list = roleDao.getPermissions(params);
        return list;
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        int getTotalCnt = roleDao.getTotal(params);
        return getTotalCnt;
    }

    @Override
    public PermissionInfo getPermissionInfo(Map<String, Object> params) {
        PermissionInfo info = roleDao.getPermissionInfo(params);
        return info;
    }

    @Override
    public PermissionInfo createPermission(PermissionInfo info, UserInfo reqUserInfo) {
        info.setId(UUID.randomUUID().toString());

        int result = roleDao.createPermission(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getType(), Constants.ACTION_CODE.PERMISSION_CREATE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

            return roleDao.getPermissionInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        info.setCreatedAt(new Timestamp(new Date().getTime()));

        return info;
    }

    @Override
    public void deletePermission(PermissionInfo info, UserInfo reqUserInfo) {
        int result = roleDao.deletePermission(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getType(), Constants.ACTION_CODE.PERMISSION_DELETE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }
    }

    @Override
    public List<PermissionInfo> createPermissionsToRole(List<PermissionInfo> infos, String roleId, UserInfo reqUserInfo) {
        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);

        for(int i=0; i<infos.size(); i++) {
            infos.get(i).setRoleId(roleId);
            createPermission(infos.get(i), reqUserInfo);
        }

        return getPermissions(params);
    }
}
