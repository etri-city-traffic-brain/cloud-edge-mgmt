package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.coredb.dao.CredentialDao;
import com.innogrid.uniq.core.model.CredentialInfo;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author kkm
 * @date 2019.3.18
 * @brief
 */
@Repository
public class CredentialDaoImpl implements CredentialDao {

    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public void setSqlSessionTemplate(@Qualifier("firstsqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    @Override
    public List<CredentialInfo> getCredentials(Map<String, Object> params) {
        return this.sqlSessionTemplate.selectList("getCredentials", params);
    }

    @Override
    public int getTotal(Map<String, Object> params) {
        return sqlSessionTemplate.selectOne("getCredentialTotal", params);
    }

    @Override
    public CredentialInfo getCredentialInfo(Map<String, Object> params) {
        return this.sqlSessionTemplate.selectOne("getCredentials", params);
    }

    @Override
    public int updateCredential(CredentialInfo info) {
        return this.sqlSessionTemplate.update("updateCredential", info);
    }

    @Override
    public int createCredential(CredentialInfo info) {
        return this.sqlSessionTemplate.insert("createCredential", info);
    }

    @Override
    public int deleteCredential(CredentialInfo info) {
        return this.sqlSessionTemplate.delete("deleteCredential", info);
    }
}
