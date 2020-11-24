package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.model.PermissionInfo;
import com.innogrid.uniq.core.model.RoleInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.dao.PermissionDao;
import com.innogrid.uniq.coredb.dao.RoleDao;
import com.innogrid.uniq.coredb.dao.UserDao;
import com.innogrid.uniq.coredb.service.ActionService;
import com.innogrid.uniq.coredb.service.RoleService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author wss
 * @date 2019.4.03
 * @brief
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final static Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private ActionService actionService;

    @Override
    public List<RoleInfo> getRoles(Map<String, Object> params) {
        List<RoleInfo> list = roleDao.getRoles(params);

        return list;
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        int totalCnt = roleDao.getTotal(params);

        return totalCnt;
    }

    @Override
    public RoleInfo getRoleInfo(Map<String, Object> params) {
        RoleInfo info = roleDao.getRoleInfo(params);

        return info;
    }

    @Override
    public RoleInfo updateRole(RoleInfo info, UserInfo reqUserInfo) {

        int result = roleDao.updateRole(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.ROLE_UPDATE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

            return roleDao.getRoleInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        return info;
    }

    @Override
    public RoleInfo createRole(RoleInfo info, UserInfo reqUserInfo) {

        info.setId(UUID.randomUUID().toString());
        info.setCreator(reqUserInfo.getId());

        int result = roleDao.createRole(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.ROLE_CREATE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

            return roleDao.getRoleInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        info.setCreatedAt(new Timestamp(new Date().getTime()));

        return info;
    }

    @Override
    public void deleteRole(RoleInfo info, UserInfo reqUserInfo) {
        int result = roleDao.deleteRole(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.ROLE_DELETE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            result = roleDao.deleteRoleUser(info);
            if(result == 1) {
                PermissionInfo permissionInfo = new PermissionInfo();
                permissionInfo.setRoleId(info.getId());
                permissionDao.deletePermission(permissionInfo);
            }

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }
    }

    @Override
    public void deleteRoleUser(RoleInfo info, UserInfo reqUserInfo) {
        int result = roleDao.deleteRoleUser(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.ROLE_USER_DELETE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }
    }

    @Override
    public List<UserInfo> createRoleToUsers(String roleId, List<UserInfo> infos, UserInfo reqUserInfo) {

        for(int i=0; i<infos.size(); i++) {
            RoleInfo info = new RoleInfo();
            info.setId(roleId);
            info.setUserId(infos.get(i).getId());

            int result = roleDao.createRoleUser(info);

            String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                    info.getName(), Constants.ACTION_CODE.ROLE_USER_ADD, Constants.HISTORY_TYPE.IAM);

            if(result == 1) {
                actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
            } else {
                actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
            }
        }

        List<UserInfo> list = userDao.getUsers(new HashMap<String, Object>() {{
            put("roleId",roleId);
        }});

        return list;
    }

    @Override
    public List<RoleInfo> createRolesToUser(List<RoleInfo> infos, String userId, UserInfo reqUserInfo) {

        for(int i=0; i<infos.size(); i++) {

            RoleInfo info = new RoleInfo();
            info.setId(infos.get(i).getId());
            info.setUserId(userId);

            int result = roleDao.createRoleUser(info);

            String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                    info.getName(), Constants.ACTION_CODE.ROLE_USER_ADD, Constants.HISTORY_TYPE.IAM);

            if(result == 1) {
                actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
            } else {
                actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
            }
        }

        List<RoleInfo> list = roleDao.getRoles(new HashMap<String, Object>() {{
            put("userId", userId);
        }});

        return list;
    }

}
