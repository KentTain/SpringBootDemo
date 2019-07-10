package com.example.util;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

public class IdSrvAccessDecisionManager implements AccessDecisionManager {
	private Logger logger = LoggerFactory.getLogger(IdSrvAccessDecisionManager.class);

    @Override 
    public boolean supports(ConfigAttribute attribute) {
            return true;
    }

    @Override 
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override 
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {

        if (authentication == null) {
            throw new AccessDeniedException("无权限！");
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // 请求路径
        String url = getUrl(object);
        // http 方法
        String httpMethod = getMethod(object);

        PreInvocationAttribute preAttr = findPreInvocationAttribute(attributes);
        
        boolean hasPerm = false;

        // request请求路径和httpMethod 和权限列表比对。
        for (GrantedAuthority authority : authorities) {
            if (!IdSrvGrantedAuthority.class.isInstance(authority))
                continue;
            IdSrvGrantedAuthority urlGrantedAuthority = (IdSrvGrantedAuthority) authority;
            if (StringUtils.isEmpty(urlGrantedAuthority.getAuthority()))
                continue;
            //如果method为null，则默认为所有类型都支持
            String httpMethod2 = (!StringUtils.isEmpty(urlGrantedAuthority.getClaimKey())) ?
                urlGrantedAuthority.getClaimKey() :
                httpMethod;
            //AntPathRequestMatcher进行匹配，url支持ant风格（如：/user/**）
            //AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(urlGrantedAuthority.getAuthority(), httpMethod2);
            //if (antPathRequestMatcher.matches(((FilterInvocation) object).getRequest())) {
            //    hasPerm = true;
            //    break;
            //}
        }

        if (!hasPerm) {
            throw new AccessDeniedException("无权限！");
        }
    }

    private PreInvocationAttribute findPreInvocationAttribute(
			Collection<ConfigAttribute> config) {
		for (ConfigAttribute attribute : config) {
			if (attribute instanceof PreInvocationAttribute) {
				return (PreInvocationAttribute) attribute;
			}
		}

		return null;
	}
    
    /**
     * 获取请求中的url(ControllerName/ActionName)
     */
    private String getUrl(Object o) {
    	if(!(o instanceof FilterInvocation)) return "";
    	
        //获取当前访问url
        String url = ((FilterInvocation) o).getRequestUrl();
        int firstQuestionMarkIndex = url.indexOf("?");
        if (firstQuestionMarkIndex != -1) {
            return url.substring(0, firstQuestionMarkIndex);
        }
        return url;
    }
    /**
     * 获取请求中的完整url(http://localhost:1003/ControllerName/ActionName)
     */
    private String getFullUrl(Object o) {
    	if(!(o instanceof FilterInvocation)) return "";
        //获取当前访问url
        String url = ((FilterInvocation) o).getFullRequestUrl();
        int firstQuestionMarkIndex = url.indexOf("?");
        if (firstQuestionMarkIndex != -1) {
            return url.substring(0, firstQuestionMarkIndex);
        }
        return url;
    }

    private String getMethod(Object o) {
        return ((FilterInvocation) o).getRequest().getMethod();
    }
}

