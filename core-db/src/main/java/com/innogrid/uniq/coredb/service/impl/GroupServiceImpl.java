package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.model.GroupInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.dao.GroupDao;
import com.innogrid.uniq.coredb.dao.UserDao;
import com.innogrid.uniq.coredb.service.ActionService;
import com.innogrid.uniq.coredb.service.GroupService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author wss
 * @date 2019.4.01
 * @brief
 */
@Service
@Transactional
public class GroupServiceImpl implements GroupService {
    private final static Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ActionService actionService;

    @Override
    public List<GroupInfo> getGroups(Map<String, Object> params) {
        List<GroupInfo> list = groupDao.getGroups(params);

        return list;
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        int groupTotalCnt = groupDao.getTotal(params);
        return groupTotalCnt;
    }

    @Override
    public GroupInfo getGroupInfo(Map<String, Object> params) {
        GroupInfo info = groupDao.getGroupInfo(params);

        return info;
    }

    @Override
    public GroupInfo updateGroup(GroupInfo info, UserInfo reqUserInfo) {
        int result = groupDao.updateGroup(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.GROUP_UPDATE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
            return groupDao.getGroupInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        return info;
    }

    @Override
    public GroupInfo createGroup(GroupInfo info, UserInfo reqUserInfo) {
        info.setId(UUID.randomUUID().toString());
        info.setCreator(reqUserInfo.getId());

        int result = groupDao.createGroup(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.GROUP_CREATE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
            return groupDao.getGroupInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        info.setCreatedAt(new Timestamp(new Date().getTime()));

        return info;
    }

    @Override
    public void deleteGroup(GroupInfo info, UserInfo reqUserInfo) {
        int result = groupDao.deleteGroup(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.GROUP_DELETE, Constants.HISTORY_TYPE.IAM);

        if(result == 1) {
            List<UserInfo> groupUser = userDao.getUsers(new HashMap<String, Object>(){{
                put("groupId", info.getId());
            }});
            for(int i=0; i<groupUser.size(); i++) {
                UserInfo user = groupUser.get(i);
                user.setGroupId("");
                userDao.updateUser(user);
            }

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }
    }
}
