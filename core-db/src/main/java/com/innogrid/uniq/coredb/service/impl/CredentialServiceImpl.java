package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.Constants;
import com.innogrid.uniq.core.model.CredentialInfo;
import com.innogrid.uniq.core.model.ProjectInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.dao.CredentialDao;
import com.innogrid.uniq.coredb.dao.ProjectDao;
import com.innogrid.uniq.coredb.service.ActionService;
import com.innogrid.uniq.coredb.service.CredentialService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kkm
 * @date 2019.3.18
 * @brief
 */
@Service
@Transactional
public class CredentialServiceImpl implements CredentialService {
    private final static Logger logger = LoggerFactory.getLogger(CredentialServiceImpl.class);

    private static List<CredentialInfo> credentialInfos = new ArrayList<>();

    @Autowired
    private CredentialDao credentialDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ActionService actionService;

    @Override
    public List<CredentialInfo> getCredentialsFromMemory(String type) {
        if(type == null) return credentialInfos;

        List<CredentialInfo> result = credentialInfos.stream().filter(credential -> credential.getType().equals(type)).collect(Collectors.toList());

        return result;
    }

    @Override
    public CredentialInfo getCredentialsFromMemoryById(String id) {
        if(id == null) return null;

        List<CredentialInfo> result = credentialInfos.stream().filter(credential -> credential.getId().equals(id)).collect(Collectors.toList());

        if(result.size() > 0) {
            return result.get(0);
        }
        return  null;
    }

    @Override
    public List<CredentialInfo> getCredentialsFromMemory() {
        if(credentialInfos.size() == 0) {
            updateCredentialsFromMemory();
        }
        return credentialInfos;
    }

    @Override
    public List<CredentialInfo> getCredentials(Map<String, Object> params) {
        List<CredentialInfo> list = credentialDao.getCredentials(params);

        list.add(new CredentialInfo("ETRI_EDGE","edge"));
        list.add(new CredentialInfo("REXGEN_EDGE","edge"));
        logger.error("list : '{}'", list);
        return list;
    }

    @Override
    public void updateCredentialsFromMemory() {
        credentialInfos = getCredentials(new HashMap<String,Object>(){{
            put("sidx", "menu");
            put("sord", "asc");
        }});
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        int totalCnt = credentialDao.getTotal(params);
        return totalCnt;
    }

    @Override
    public CredentialInfo getCredentialInfo(Map<String, Object> params) {
        CredentialInfo info = credentialDao.getCredentialInfo(params);

        return info;
    }

    @Override
    public CredentialInfo updateCredential(CredentialInfo info, UserInfo reqUserInfo) {
        int result = credentialDao.updateCredential(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.CREDENTIAL_UPDATE, Constants.HISTORY_TYPE.CREDENTIAL);

        if(result == 1) {

            updateCredentialsFromMemory();

            ProjectInfo projectInfo = new ProjectInfo();
            projectInfo.setCloudId(info.getId());
            projectDao.deleteProject(projectInfo);

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

            return credentialDao.getCredentialInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        return info;
    }

    @Override
    public CredentialInfo createCredential(CredentialInfo info, UserInfo reqUserInfo) {
        info.setId(UUID.randomUUID().toString());

        int result = credentialDao.createCredential(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.CREDENTIAL_CREATE, Constants.HISTORY_TYPE.CREDENTIAL);

        if(result == 1) {

            updateCredentialsFromMemory();

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

            return credentialDao.getCredentialInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        info.setCreatedAt(new Timestamp(new Date().getTime()));

        return info;
    }

    @Override
    public CredentialInfo createCredentialApi(CredentialInfo info) {
        info.setId(UUID.randomUUID().toString());

        int result = credentialDao.createCredential(info);

        String groupId = "";
        String getId = "admin";

        String actionId = actionService.initAction(groupId, getId, info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.CREDENTIAL_CREATE, Constants.HISTORY_TYPE.CREDENTIAL);

        if(result == 1) {

            updateCredentialsFromMemory();

            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);

            return credentialDao.getCredentialInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }

        info.setCreatedAt(new Timestamp(new Date().getTime()));

        return info;
    }

    @Override
    public void deleteCredential(CredentialInfo info, UserInfo reqUserInfo) {

        int result = credentialDao.deleteCredential(info);

        String actionId = actionService.initAction(reqUserInfo.getGroupId(), reqUserInfo.getId(), info.toString(), info.getId(),
                info.getName(), Constants.ACTION_CODE.CREDENTIAL_DELETE, Constants.HISTORY_TYPE.CREDENTIAL);


        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setCloudId(info.getId());
        projectDao.deleteProject(projectInfo);

        updateCredentialsFromMemory();

        if(result == 1) {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.SUCCESS);
        } else {
            actionService.setActionResult(actionId, Constants.ACTION_RESULT.FAILED);
        }
    }
}
