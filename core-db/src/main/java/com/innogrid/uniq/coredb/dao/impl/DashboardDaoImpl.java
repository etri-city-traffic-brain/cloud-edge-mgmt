package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.ServiceDashboardInfo;
import com.innogrid.uniq.coredb.dao.DashboardDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.4.24
 * @brief
 */
@Repository
public class DashboardDaoImpl implements DashboardDao {

	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Override
	public List<ServiceDashboardInfo> getDashboards(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getDashboard", params);
	}

	@Override
	public ServiceDashboardInfo getDashboard(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getDashboard", params);
	}

	@Override
	public int updateDashboard(ServiceDashboardInfo info) {
		return this.sqlSessionTemplate.update("updateDashboard", info);
	}

	@Override
	public int createDashboard(ServiceDashboardInfo info) {
		return this.sqlSessionTemplate.insert("createDashboard", info);
	}

	@Override
	public int deleteDashboard(ServiceDashboardInfo info) {
		return this.sqlSessionTemplate.delete("deleteDashboard", info);
	}

	@Override
	public int getIDCount(String id) {
		return this.sqlSessionTemplate.selectOne("getDashboardIdCount", id);
	}
}
