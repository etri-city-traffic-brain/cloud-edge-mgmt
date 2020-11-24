package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.ProjectInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.05
 * @brief
 */
public interface ProjectDao {
    List<ProjectInfo> getProjects(Map<String, Object> params);

    int getTotal(Map<String, Object> params);

    ProjectInfo getProjectInfo(Map<String, Object> params);

    int createProject(ProjectInfo info);

    int deleteProject(ProjectInfo info);
}

