package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.HistoryInfo;
import com.innogrid.uniq.coredb.dao.HistoryDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 15. 4. 3.
 */
@Repository
public class HistoryDaoImpl implements HistoryDao {
    
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    @Override
    public List<HistoryInfo> getHistorys(Map<String, Object> params) {
        return this.sqlSessionTemplate.selectList("getHistorys", params);
    }

    @Override
    public HistoryInfo getHistory(Map<String, Object> params) {
        return this.sqlSessionTemplate.selectOne("getHistorys", params);
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        return sqlSessionTemplate.selectOne("getHistoryTotal", params);
    }

    @Override
    public int insertHistory(HistoryInfo info) {
//        return this.sqlSessionTemplate.insert("insertHistory", info);
        return 1;
    }

    @Override
    public int deleteHistory(HistoryInfo info) {
        return this.sqlSessionTemplate.delete("deleteHistory", info);
    }
}
