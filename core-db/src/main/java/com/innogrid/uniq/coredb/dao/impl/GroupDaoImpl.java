package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.GroupInfo;
import com.innogrid.uniq.coredb.dao.GroupDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wss
 * @date 2019.4.01
 * @brief
 */
@Repository
public class GroupDaoImpl implements GroupDao {

	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	public void setSqlSessionTemplate(@Qualifier("firstsqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Override
	public List<GroupInfo> getGroups(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getGroups", params);
	}

	@Override
	public int getTotal(Map<String, Object> params) {
		return sqlSessionTemplate.selectOne("getGroupTotal", params);
	}

	@Override
	public GroupInfo getGroupInfo(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getGroups", params);
	}

	@Override
	public int updateGroup(GroupInfo info) {
		return this.sqlSessionTemplate.update("updateGroup", info);
	}

	@Override
	public int createGroup(GroupInfo info) {
		return this.sqlSessionTemplate.insert("createGroup", info);
	}

	@Override
	public int deleteGroup(GroupInfo info) {
		return this.sqlSessionTemplate.delete("deleteGroup", info);
	}
}
