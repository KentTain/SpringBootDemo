package com.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.example.model.TenantInfo;
import com.example.multitenancy.TenantContext;

/**
 * 根据用户的域名获取相对应的租户信息，并设置到TenantContext中
 * 
 * @author tianchangjun
 * @version 1.0
 */

@SuppressWarnings("all")
@Component
public class TenantInterceptor extends HandlerInterceptorAdapter {
	private Logger logger = LoggerFactory.getLogger(TenantInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 获取域名
		String serverName = request.getServerName();
		if (serverName == null)
			throw new RuntimeException("未找到主机名");

		TenantInfo tenant = TenantContext.GetTenantByDomain(serverName);

		if (tenant == null)
			throw new RuntimeException("未找到相关租户信息");

		logger.debug(String.format("-----TenantInterceptor setCurrentTenant %s %s", tenant.getTenantId(), serverName));

		TenantContext.setCurrentTenant(tenant.getTenantId());

		request.getSession().setAttribute("tenantId", tenant.getTenantId());
		request.getSession().setAttribute("domain", serverName);

		return true;
	}
}