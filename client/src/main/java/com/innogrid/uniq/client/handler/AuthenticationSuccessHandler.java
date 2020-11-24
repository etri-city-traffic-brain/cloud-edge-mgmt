package com.innogrid.uniq.client.handler;

import com.innogrid.uniq.core.model.UserInfo;
import com.innogrid.uniq.coredb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

public class AuthenticationSuccessHandler extends
        SimpleUrlAuthenticationSuccessHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);
	
	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {

		UserInfo info = userService.getUserInfo(new HashMap<String, Object>(){{put("id", authentication.getName());}});

//		if(info == null) {
//			Map<String, Object> oAuth2UserInfo = ((OAuth2User)authentication.getPrincipal()).getAttributes();
//
//			info = new UserInfo();
//
//			info.setNewId((String)oAuth2UserInfo.get("user_id"));
//			info.setName((String)oAuth2UserInfo.get("nickname"));
//			info.setEmail((String)oAuth2UserInfo.get("email"));
//			info.setContract((String)oAuth2UserInfo.get("phone"));
//			info.setGroupId("");
//			info.setPassword("");
//			info.setEnabled(true);
//
//			info = userService.createUser(info, info);
//		}
//
		request.getSession().setAttribute("userInfo", info);

		long nowTime = System.currentTimeMillis();

		Timestamp lastVisit = info.getLogin();
		info.setLogin(new Timestamp(nowTime));
		info.setPassword(null);
		userService.updateUser(info, null);

		if(lastVisit == null) {
			lastVisit = new Timestamp(nowTime);
		}

		request.getSession().setAttribute("lastVisit", lastVisit);

		this.setDefaultTargetUrl("/");
		super.onAuthenticationSuccess(request, response, authentication);
		
	}
	
}
