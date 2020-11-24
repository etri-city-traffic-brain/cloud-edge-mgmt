package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.model.AuthUser;
import com.innogrid.uniq.core.model.PermissionInfo;
import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.dao.PermissionDao;
import com.innogrid.uniq.coredb.dao.UserDao;
import com.innogrid.uniq.coredb.service.AuthService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public static final String prefix = "ROLE_";

    @Override
    public UserInfo getUserInfo(String id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
		params.put("all", true);

        return userDao.getUserInfo(params);
    }

	@Autowired
	private UserDao userDao;

    @Autowired
	private PermissionDao permissionDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("[{}] Load User By Username", username);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", username);
		params.put("all", true);
		UserInfo userInfo = userDao.getUserInfo(params);
		if (userInfo == null) {
			logger.error("Username Not Found : '{}'", username);
			throw new UsernameNotFoundException("Username Not Found : " + username);
		}

		AuthUser loginUser = new AuthUser();
		loginUser.setUsername(userInfo.getId());
		loginUser.setPassword(userInfo.getPassword());
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if(userInfo.getAdmin()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		List<PermissionInfo> permissions = permissionDao.getUserPermissions(new HashMap<String, Object>(){{
			put("id", loginUser.getUsername());
		}});

		for(int i=0; i<permissions.size(); i++) {
			authorities.add(new SimpleGrantedAuthority(prefix + permissions.get(i).getType()));
		}

		loginUser.setAuthorities(authorities);

		logger.info("[{}] Load User By Username Complete", username);
		return loginUser;
	}
}
