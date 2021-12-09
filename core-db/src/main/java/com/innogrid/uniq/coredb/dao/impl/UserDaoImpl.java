package com.innogrid.uniq.coredb.dao.impl;

import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.dao.UserDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao {

	private SqlSessionTemplate sqlSessionTemplate;

	@Autowired
	public void setSqlSessionTemplate(@Qualifier("firstsqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Override
	public List<UserInfo> getUsers(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectList("getUsers", params);
	}

	@Override
	public UserInfo getUserInfo(Map<String, Object> params) {
		return this.sqlSessionTemplate.selectOne("getUsers", params);
	}

	@Override
	public int getTotal(Map<String, Object> params) {
		return sqlSessionTemplate.selectOne("getUserTotal", params);
	}

	@Override
	public int createUser(UserInfo info) {
		return this.sqlSessionTemplate.insert("createUser", info);
	}

	@Override
	public int updateUser(UserInfo info) {
		return this.sqlSessionTemplate.update("updateUser", info);
	}

	@Override
	public int deleteUser(UserInfo info) {
		return this.sqlSessionTemplate.delete("deleteUser", info);
	}

	@Override
	public int getUserAuthentication(Map<String,Object> params) {
		return this.sqlSessionTemplate.selectOne("getUserAuthentication", params);
	}

    @Override
    public int getIDCount(String id) {
        return this.sqlSessionTemplate.selectOne("getIDCount", id);
    }
}
