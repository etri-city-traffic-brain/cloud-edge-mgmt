package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.model.RoleInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.dao.RoleDao;
import com.innogrid.uniq.coredb.dao.UserDao;
import com.innogrid.uniq.coredb.service.ActionService;
import com.innogrid.uniq.coredb.service.UserService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wss on 19. 4. 01.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ActionService actionService;

    @Override
    public List<UserInfo> getUserInfos(Map<String, Object> params) {
        List<UserInfo> list = userDao.getUsers(params);

        return list;
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        int totalCnt = userDao.getTotal(params);

        return totalCnt;
    }

    @Override
    public UserInfo getUserInfo(Map<String, Object> params) {
        UserInfo userInfo = userDao.getUserInfo(params);

        return userInfo;
    }

    @Override
    public UserInfo createUser(UserInfo info, UserInfo reqInfo) {
        String enPassword = passwordEncoder.encode(info.getPassword());
        info.setPassword(enPassword);
        info.setId(info.getNewId());

        int result = userDao.createUser(info);

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.USER_CREATE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

            return userDao.getUserInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        return info;
    }

    @Override
    public UserInfo updateUser(UserInfo info, UserInfo reqInfo) {
        if(info.getPassword() != null && !info.getPassword().trim().equals("")) {
            String enPassword = passwordEncoder.encode(info.getPassword());
            info.setPassword(enPassword);
        } else {
            info.setPassword(null);
        }

        int result = userDao.updateUser(info);

        String actionId = null;
        if(reqInfo != null) {
            actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), info.toString(), info.getId(),
                    info.getName(), Constants.ACTION_CODE.USER_UPDATE, Constants.HISTORY_TYPE.IAM);
        }

        if(result == 1) {
            if(actionId != null) {
                actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
            }
            return userDao.getUserInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        return info;
    }

    @Override
    public void deleteUser(UserInfo info, UserInfo reqInfo) {
        int result = userDao.deleteUser(info);

        String actionId = actionService.initAction(reqInfo.getGroupId(), reqInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.USER_DELETE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            RoleInfo roleInfo = new RoleInfo();
            roleInfo.setUserId(info.getId());
            roleDao.deleteRoleUser(roleInfo);

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }
    }

    @Override
    public int getUserAuthentication(String userId, String password) {
        String enPassword = passwordEncoder.encode(password);

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id", userId);
        params.put("password", enPassword);

        int result =userDao.getUserAuthentication(params);
        return result;
    }

    @Override
    public List<UserInfo> createGroupUsers(List<UserInfo> infos, String groupId, UserInfo reqUser) {

        for(int i=0; i<infos.size(); i++) {
            UserInfo info = new UserInfo();
            info.setId(infos.get(i).getId());
            info.setGroupId(groupId);

            int result = userDao.updateUser(info);

            String actionId = actionService.initAction(reqUser.getGroupId(), reqUser.getId(), info.toString(), groupId,
                    info.getName(), Constants.ACTION_CODE.GROUP_USER_ADD, Constants.HISTORY_TYPE.IAM);

            if(result == 1) {
                actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
            } else {
                actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
            }
        }

        List<UserInfo> list = userDao.getUsers(new HashMap<String, Object>() {{
            put("groupId", groupId);
        }});

        return list;
    }
}
