package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.ActionInfo;
import com.innogrid.uniq.coredb.dao.ActionDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.6.22
 * @brief
 */
@Repository
public class ActionDaoImpl implements ActionDao {

	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Override
	public List<ActionInfo> getActions(Map<String, Object> params) {
		return sqlSessionTemplate.selectList("getActions", params);
	}

	@Override
	public int getActionsTotal(Map<String, Object> params) {
		return sqlSessionTemplate.selectOne("getActionsTotal", params);
	}


	@Override
	public ActionInfo getAction(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getActions", params);
	}

	@Override
	public int createAction(ActionInfo info) {
		return this.sqlSessionTemplate.insert("createAction", info);
	}

	@Override
	public int updateAction(ActionInfo info) {
		return this.sqlSessionTemplate.update("updateAction", info);
	}

	@Override
	public int deleteAction(ActionInfo info) {
		return this.sqlSessionTemplate.delete("deleteAction", info);
	}

	@Override
	public int getIDCount(String id) {
		return this.sqlSessionTemplate.selectOne("getActionIDCount", id);
	}
}
