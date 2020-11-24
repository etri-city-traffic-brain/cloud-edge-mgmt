package com.innogrid.uniq.coredb.dao;


import com.innogrid.uniq.core.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 15. 3. 31.
 */
public interface UserDao {
    public List<UserInfo> getUsers(Map<String, Object> params);

    public UserInfo getUserInfo(Map<String, Object> params);

    public int getTotal(Map<String, Object> params);

    public int createUser(UserInfo info);

    public int updateUser(UserInfo info);

    public int deleteUser(UserInfo info);

    public int getUserAuthentication(Map<String, Object> params);

    public int getIDCount(String id);
}
