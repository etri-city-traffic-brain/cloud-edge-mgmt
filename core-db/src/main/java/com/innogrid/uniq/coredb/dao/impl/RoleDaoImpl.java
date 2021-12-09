package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.RoleInfo;
import com.innogrid.uniq.coredb.dao.RoleDao;
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
public class RoleDaoImpl implements RoleDao {

	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	public void setSqlSessionTemplate(@Qualifier("firstsqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Override
	public List<RoleInfo> getRoles(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getRoles", params);
	}

	@Override
	public int getTotal(Map<String, Object> params) {
		return sqlSessionTemplate.selectOne("getRoleTotal", params);
	}

	@Override
	public RoleInfo getRoleInfo(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getRoles", params);
	}

	@Override
	public int updateRole(RoleInfo info) {
		return this.sqlSessionTemplate.update("updateRole", info);
	}

	@Override
	public int createRole(RoleInfo info) {
		return this.sqlSessionTemplate.insert("createRole", info);
	}

	@Override
	public int deleteRole(RoleInfo info) {
		return this.sqlSessionTemplate.delete("deleteRole", info);
	}



	@Override
	public int deleteRoleUser(RoleInfo info) {
		return this.sqlSessionTemplate.delete("deleteRoleUser", info);
	}

	@Override
	public int createRoleUser(RoleInfo info) {
		return this.sqlSessionTemplate.insert("createRoleUser", info);
	}

}
