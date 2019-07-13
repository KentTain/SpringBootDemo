package com.example.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

@Service
public class IdSrvSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
	private final Map<RequestMatcher, Collection<ConfigAttribute>> resourceMap;

	public IdSrvSecurityMetadataSource()
	{
		resourceMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();
	}
	
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		Set<ConfigAttribute> allAttributes = new HashSet<>();

		for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : resourceMap.entrySet()) {
			allAttributes.addAll(entry.getValue());
		}

		return allAttributes;
	}

	public boolean supports(Class<?> clazz) {
		return true;
	}
	/**
	 * @PostConstruct是Java EE 5引入的注解，
	 * Spring允许开发者在受管Bean中使用它。当DI容器实例化当前受管Bean时，
	 * @PostConstruct注解的方法会被自动触发，从而完成一些初始化工作，
	 * 
	 * //加载所有资源与权限的关系
	 */
	@PostConstruct
	private void loadResourceDefine() {
		//System.err.println("-----------MySecurityMetadataSource loadResourceDefine ----------- ");
		//List<Resources> menus = menuMapper.queryAll(new Resources());
		//for (Resources m : menus) 
		{
			Collection<ConfigAttribute> configAttributes = new ArrayList<ConfigAttribute>();
			// TODO:ZZQ 通过资源名称来表示具体的权限 注意：必须"ROLE_"开头
			// 关联代码：applicationContext-security.xml
			// 关联代码：com.huaxin.security.MyUserDetailServiceImpl#obtionGrantedAuthorities
			//ConfigAttribute configAttribute = new SecurityConfig("ROLE_" + m.getResKey());
			//configAttributes.add(configAttribute);
			//resourceMap.put(m.getResUrl(), configAttributes);
		}
	}
	//返回所请求资源所需要的权限
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		final HttpServletRequest request = ((FilterInvocation) object).getRequest();
		String requestUrl = request.getRequestURI();
		System.out.println("-----IdSrvSecurityMetadataSource requestUrl is " + requestUrl);
		if(resourceMap.size() < 0) {
			loadResourceDefine();
		}

		if(requestUrl.indexOf("?")>-1){
			requestUrl=requestUrl.substring(0,requestUrl.indexOf("?"));
		}
		Collection<ConfigAttribute> configAttributes = resourceMap.get(requestUrl);
//		if(configAttributes == null){
//			Collection<ConfigAttribute> returnCollection = new ArrayList<ConfigAttribute>();
//			 returnCollection.add(new SecurityConfig("ROLE_NO_USER")); 
//			return returnCollection;
//		}
		return configAttributes;
	}


}
