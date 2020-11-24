package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.model.ActionInfo;
import com.innogrid.uniq.coredb.dao.ActionDao;
import com.innogrid.uniq.coredb.service.ActionService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by kkm on 19. 6. 22
 */
@Service
@Transactional
public class ActionServiceImpl implements ActionService {
    private final static Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);

    @Autowired
    private ActionDao actionDao;

    @Override
    public List<ActionInfo> actions(Map<String, Object> params) {
        List<ActionInfo> list = actionDao.getActions(params);
        return list;
    }

    @Override
    public int getActionsTotal(Map<String, Object> params) {
        return actionDao.getActionsTotal(params);
    }

    @Override
    public ActionInfo action(Map<String, Object> params) {
        ActionInfo info = actionDao.getAction(params);
        return actionDao.getAction(params);
    }

    @Override
    public int updateAction(ActionInfo info) {
        int result = actionDao.updateAction(info);

        /*if(result == 1) {

            return dashboardDao.getDashboard(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        }*/

        return result;
    }

    @Override
    public int createAction(ActionInfo info) {
        int result = actionDao.createAction(info);

        /*if(result == 1) {
            return dashboardDao.getDashboard(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        }*/

        return result;
    }

    @Override
    public int getIDCount(String id) {
        int idCount = actionDao.getIDCount(id);
        return idCount;
    }

    @Override
    public String initAction(String groupId, String userId, String content, String targetId,
                             String targetName, Constants.ACTION_CODE actionCode, Constants.HISTORY_TYPE type) {
        if(groupId == null) groupId = "";
        String actionId = UUID.randomUUID().toString();
        ActionInfo info = new ActionInfo();
        info.setId(actionId);
        info.setContent(content);
        info.setGroupId(groupId);
        info.setUserId(userId);
        info.setTargetId(targetId);
        info.setTargetName(targetName);
        info.setActionCode(actionCode);
        info.setType(type);

        createAction(info);

        return actionId;
    }

    @Override
    public void setActionResult(String actionId, Constants.ACTION_RESULT opResult) {
        this.setActionResult(actionId, opResult, null);
    }

    @Override
    public void setActionResult(String actionId, Constants.ACTION_RESULT opResult, String resultDetail) {
        ActionInfo info = new ActionInfo();
        info.setId(actionId);
        info.setResult(opResult);
        info.setResultDetail(resultDetail);

        updateAction(info);
    }
}
