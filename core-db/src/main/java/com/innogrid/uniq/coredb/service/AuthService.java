package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.UserInfo;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    public UserInfo getUserInfo(String id);
}
