package com.innogrid.uniq.client.config;

import com.innogrid.uniq.client.handler.AuthenticationFailureHandler;
import com.innogrid.uniq.client.handler.AuthenticationSuccessHandler;
import com.innogrid.uniq.coredb.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	///////////// SSO = 주석, NON SSO = 주석해제
	@Bean
	public AuthenticationSuccessHandler getSuccessHandler() {
		return new AuthenticationSuccessHandler();
	}

	@Bean
	public AuthenticationFailureHandler getFailureHandler() {
		return new AuthenticationFailureHandler();
	}
	/////////////

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		///////////// SSO = 주석, NON SSO = 주석해제
		http
				.authorizeRequests()
				.antMatchers("/", "/index", "/index/logo", "/login", "/api/**", "/actuator/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.successHandler(getSuccessHandler())
				.failureHandler(getFailureHandler())
				.permitAll()
				.and()
				.logout()
				.logoutUrl("/logout")
//                .logoutSuccessHandler(getLogoutSuccessHandler())
				.invalidateHttpSession(false)
				.permitAll()
				.and()
				.sessionManagement()
				.invalidSessionUrl("/login?error=sessionExpired");
		http.csrf().disable();
	}

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private AuthService userAuthService;

	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userAuthService).passwordEncoder(passwordEncoder);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
				.antMatchers("/i18n/**")
				.antMatchers("/static/**")
				.antMatchers("/css/**")
				.antMatchers("/js/**")
				.antMatchers("/images/**");
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
