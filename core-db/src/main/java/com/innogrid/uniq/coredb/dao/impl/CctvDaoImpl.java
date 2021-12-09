package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.CctvInfo;
import com.innogrid.uniq.core.model.RoleInfo;
import com.innogrid.uniq.coredb.dao.CctvDao;
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
public class CctvDaoImpl implements CctvDao {

	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	public void setSqlSessionTemplate(@Qualifier("secondsqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Override
	public List<CctvInfo> getCctvs(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getCctvs_connect_svr1", params);
	}

	@Override
	public int getTotal(Map<String, Object> params) {
		return sqlSessionTemplate.selectOne("getCctvTotal_connect_svr1", params);
	}

	@Override
	public CctvInfo getCctvInfo(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getCctvs_connect_svr1", params);
	}

	@Override
	public List<CctvInfo> getCctvs2(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getCctvs_connect_svr2", params);
	}

	@Override
	public int getTotal2(Map<String, Object> params) {
		return sqlSessionTemplate.selectOne("getCctvTotal_connect_svr2", params);
	}

	@Override
	public CctvInfo getCctvInfo2(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getCctvs_connect_svr2", params);
	}
}
