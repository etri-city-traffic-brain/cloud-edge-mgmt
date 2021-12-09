package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.ProjectInfo;
import com.innogrid.uniq.coredb.dao.ProjectDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.05
 * @brief
 */
@Repository
public class ProjectDaoImpl implements ProjectDao {

	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	public void setSqlSessionTemplate(@Qualifier("firstsqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Override
	public List<ProjectInfo> getProjects(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getProjects", params);
	}

	@Override
	public int getTotal(Map<String, Object> params) {
		return sqlSessionTemplate.selectOne("getProjectTotal", params);
	}

	@Override
	public ProjectInfo getProjectInfo(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getProjects", params);
	}

	@Override
	public int createProject(ProjectInfo info) {
		return this.sqlSessionTemplate.insert("createProject", info);
	}

	@Override
	public int deleteProject(ProjectInfo info) {
		return this.sqlSessionTemplate.delete("deleteProject", info);
	}
}
