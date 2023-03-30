package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.MeterServerAccumulateInfo;
import com.innogrid.uniq.core.model.MeterServerInfo;
import com.innogrid.uniq.coredb.dao.MeterDao;
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
public class MeterDaoImpl implements MeterDao {

	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	public void setSqlSessionTemplate(SqlSessionTemplate firstsqlSessionTemplate) {
		this.sqlSessionTemplate = firstsqlSessionTemplate;
	}

	@Override
	public List<MeterServerInfo> getMeterServers(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getMeterServer", params);
	}

	@Override
	public MeterServerInfo getMeterServer(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getMeterServer", params);
	}

	@Override
	public int updateMeterServer(MeterServerInfo info) {
		return this.sqlSessionTemplate.update("updateMeterServer", info);
	}

	@Override
	public int createMeterServer(MeterServerInfo info) {
		return this.sqlSessionTemplate.insert("createMeterServer", info);
	}

	@Override
	public int getMeterServerIDCount(MeterServerInfo info) {
		return this.sqlSessionTemplate.selectOne("getMeterServerIDCount", info);
	}

	@Override
	public List<MeterServerAccumulateInfo> getMeterServerAccumulates(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getMeterServerAccumulate", params);
	}

	@Override
	public MeterServerAccumulateInfo getMeterServerAccumulate(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getMeterServerAccumulate", params);
	}

	@Override
	public int updateMeterServerAccumulate(MeterServerAccumulateInfo info) {
		return this.sqlSessionTemplate.update("updateMeterServerAccumulate", info);
	}

	@Override
	public int createMeterServerAccumulate(MeterServerAccumulateInfo info) {
		return this.sqlSessionTemplate.insert("createMeterServerAccumulate", info);
	}

	@Override
	public int getMeterServerAccumulateIDCount(MeterServerAccumulateInfo info) {
		return this.sqlSessionTemplate.selectOne("getMeterServerAccumulateIDCount", info);
	}
}
