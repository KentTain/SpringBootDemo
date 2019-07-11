package com.example.util;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.TenantInfo;
import com.example.Interceptor.TenantContext;

/**
 * 核心的InterceptorStatusToken token =
 * super.beforeInvocation(fi);会调用我们定义的accessDecisionManager:decide(Object
 * object)和securityMetadataSource
 * 
 * :getAttributes(Object object)方法。 自己实现的过滤用户请求类，也可以直接使用
 * FilterSecurityInterceptor
 * 
 * AbstractSecurityInterceptor有三个派生类：
 * FilterSecurityInterceptor，负责处理FilterInvocation，实现对URL资源的拦截。
 * MethodSecurityInterceptor，负责处理MethodInvocation，实现对方法调用的拦截。
 * AspectJSecurityInterceptor，负责处理JoinPoint，主要是用于对切面方法(AOP)调用的拦截。
 * 
 * 还可以直接使用注解对Action方法进行拦截，例如在方法上加： @PreAuthorize("hasRole('ROLE_SUPER')")
 */
@Component
public class IdSrvSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {
	private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied";
	private boolean observeOncePerRequest = true;

	// 与applicationContext-security.xml里的myFilter的属性securityMetadataSource对应，
	// 其他的两个组件，已经在AbstractSecurityInterceptor定义
	@Autowired
	private IdSrvSecurityMetadataSource securityMetadataSource;
	@Autowired
	private IdSrvAccessDecisionManager accessDecisionManager;

	@PostConstruct
	public void init() {
		super.setAccessDecisionManager(accessDecisionManager);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String serverName = request.getServerName();
		if (serverName == null)
			throw new ServletException("未找到主机名");
		TenantInfo tenant = TenantContext.GetTenantByDomain(serverName);
		if (tenant == null)
			throw new ServletException("未找到相关租户信息");
		
		System.out.println(String.format("-----IdSrvSecurityInterceptor setCurrentTenant %s in domain: %s", tenant.getTenantId(),
				serverName));
		//设置当前访问对象的租户Id
		TenantContext.setCurrentTenant(tenant);
		
		//FilterInvocation fi = new FilterInvocation(request, response, chain);
		//invoke(fi);
	}

	public void invoke(FilterInvocation fi) throws IOException, ServletException {
		if ((fi.getRequest() != null) && (fi.getRequest().getAttribute(FILTER_APPLIED) != null)
				&& observeOncePerRequest) {
			// filter already applied to this request and user wants us to observe
			// once-per-request handling, so don't re-do security checking
			fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
		} else {
			// first time this request being called, so perform security checking
			if (fi.getRequest() != null && observeOncePerRequest) {
				fi.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
			}

			InterceptorStatusToken token = super.beforeInvocation(fi);

			try {
				fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
			} finally {
				super.finallyInvocation(token);
			}

			super.afterInvocation(token, null);
		}
	}

	@Override
	public Class<?> getSecureObjectClass() {
		// 下面的MyAccessDecisionManager的supports方面必须放回true,否则会提醒类型错误
		return FilterInvocation.class;
	}

	@Override
	public SecurityMetadataSource obtainSecurityMetadataSource() {
		return this.securityMetadataSource;
	}

	public boolean isObserveOncePerRequest() {
		return observeOncePerRequest;
	}

	public void setObserveOncePerRequest(boolean observeOncePerRequest) {
		this.observeOncePerRequest = observeOncePerRequest;
	}
}
