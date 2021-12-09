package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.PermissionInfo;
import com.innogrid.uniq.coredb.dao.PermissionDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.03
 * @brief
 */
@Repository
public class PermissionDaoImpl implements PermissionDao {

	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	public void setSqlSessionTemplate(@Qualifier("firstsqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Override
	public List<PermissionInfo> getPermissions(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getPermissions", params);
	}

	@Override
	public List<PermissionInfo> getUserPermissions(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getUserPermissions", params);
	}

	@Override
	public int getTotal(Map<String, Object> params) {
		return sqlSessionTemplate.selectOne("getPermissionTotal", params);
	}

	@Override
	public PermissionInfo getPermissionInfo(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getPermissions", params);
	}

	@Override
	public int createPermission(PermissionInfo info) {
		return this.sqlSessionTemplate.insert("createPermission", info);
	}

	@Override
	public int deletePermission(PermissionInfo info) {
		return this.sqlSessionTemplate.delete("deletePermission", info);
	}

}
