package com.qiguliuxing.dts.admin.config;

import com.qiguliuxing.dts.admin.shiro.AdminAuthorizingRealm;
import com.qiguliuxing.dts.admin.shiro.AdminWebSessionManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

	@Bean
	public Realm realm() {
		return new AdminAuthorizingRealm();
	}

	@Bean
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();


		filterChainDefinitionMap.put("/admin/auth/login", "anon");
		filterChainDefinitionMap.put("/admin/auth/userLogin", "anon");
		filterChainDefinitionMap.put("/admin/auth/userRegister", "anon");
		filterChainDefinitionMap.put("/admin/auth/sendEmail", "anon");
		filterChainDefinitionMap.put("/admin/auth/captchaImage", "anon");
		filterChainDefinitionMap.put("/admin/auth/401", "anon");
		filterChainDefinitionMap.put("/admin/auth/index", "anon");
		filterChainDefinitionMap.put("/admin/auth/403", "anon");

		 filterChainDefinitionMap.put("/admin/**", "authc");
		shiroFilterFactoryBean.setLoginUrl("/admin/auth/401");
		shiroFilterFactoryBean.setSuccessUrl("/admin/auth/index");
		shiroFilterFactoryBean.setUnauthorizedUrl("/admin/auth/403");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
		ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
		bean.setSecurityManager(securityManager);
		//---------??????------

		// filterChainDefinitions?????????map????????????LinkedHashMap??????????????????????????????
		Map<String, String> filterMap = new LinkedHashMap<>();
		// ??????????????????????????????
		filterMap.put("/swagger-ui.html", "anon");
		filterMap.put("/swagger-ui.html/**", "anon");
		filterMap.put("/swagger-resources/**", "anon");
		filterMap.put("/webjars/**", "anon");
		filterMap.put("/v2/**", "anon");
		// ?????? shiro ?????????
		bean.setFilterChainDefinitionMap(filterMap);
		return bean;
	}

	@Bean
	public SessionManager sessionManager() {
		AdminWebSessionManager mySessionManager = new AdminWebSessionManager();
		return mySessionManager;
	}

	@Bean
	public DefaultWebSecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(realm());
		securityManager.setSessionManager(sessionManager());
		return securityManager;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
		creator.setProxyTargetClass(true);
		return creator;
	}
}
