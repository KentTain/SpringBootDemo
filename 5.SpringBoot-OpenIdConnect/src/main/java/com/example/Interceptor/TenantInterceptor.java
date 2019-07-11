package com.example.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.example.TenantInfo;
import com.example.Interceptor.TenantContext;

/**
 * 根据用户的域名获取相对应的租户信息，并设置到TenantContext中
 * 
 * @author tianchangjun
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

		System.out.println(String.format("-----TenantInterceptor setCurrentTenant %s in domain: %s", tenant.getTenantId(),
				serverName));

		//设置当前访问对象的租户Id
		TenantContext.setCurrentTenant(tenant);
		//根据租户数据，设置租户的数据源
		//TenantDataSourceProvider.addDataSource(tenant);
		
		request.getSession().setAttribute("tenantId", tenant.getTenantId());
		request.getSession().setAttribute("domain", serverName);
		request.getServletContext().setAttribute("tenantId", tenant);
		return true;
	}
}