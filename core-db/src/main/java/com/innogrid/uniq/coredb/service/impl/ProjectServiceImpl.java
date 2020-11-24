package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.model.ProjectInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.dao.ProjectDao;
import com.innogrid.uniq.coredb.service.ProjectService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author wss
 * @date 2019.4.05
 * @brief
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectDao projectDao;

    @Override
    public List<ProjectInfo> getProjects(Map<String, Object> params) {

        return projectDao.getProjects(params);
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        int projectTotalCnt = projectDao.getTotal(params);
        return projectTotalCnt;
    }

    @Override
    public ProjectInfo getProjectInfo(Map<String, Object> params) {

        return projectDao.getProjectInfo(params);
    }

    @Override
    public ProjectInfo createProject(ProjectInfo info, UserInfo reqUserInfo) {

        info.setId(UUID.randomUUID().toString());

        int result = projectDao.createProject(info);

        if(result == 1) {

            return projectDao.getProjectInfo(new HashMap<String, Object>(){{
                put("id", info.getId());
            }});
        }

        info.setCreatedAt(new Timestamp(new Date().getTime()));

        return info;
    }

    @Override
    public void deleteProject(ProjectInfo info, UserInfo reqUserInfo) {
        int result = projectDao.deleteProject(info);
        if(result == 1) {

        }
    }

    @Override
    public List<ProjectInfo> syncProject(List<ProjectInfo> infos, String groupId, UserInfo reqUserInfo) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);

        List<ProjectInfo> dbList = getProjects(params);
        for(int i=0; i<dbList.size(); i++) {
            deleteProject(dbList.get(i), reqUserInfo);
        }

        for(int i=0; i<infos.size(); i++) {
            ProjectInfo info = infos.get(i);
            createProject(info, reqUserInfo);
        }

        return getProjects(params);
    }
}
