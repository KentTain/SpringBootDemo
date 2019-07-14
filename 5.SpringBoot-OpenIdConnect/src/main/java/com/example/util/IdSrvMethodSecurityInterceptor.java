package com.example.util;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.stereotype.Component;

import com.example.annotation.PermissionAnnotation;

@Component
public class IdSrvMethodSecurityInterceptor extends AbstractSecurityInterceptor implements MethodInterceptor {
// ~ Instance fields
// ================================================================================================
	@Autowired
	private IdSrvSecurityMetadataSource securityMetadataSource;
	@Autowired
	private IdSrvAccessDecisionManager accessDecisionManager;
	
	@PostConstruct
	public void init() {
		super.setAccessDecisionManager(accessDecisionManager);
	}

// ~ Methods
// ========================================================================================================

	public Class<?> getSecureObjectClass() {
		return MethodInvocation.class;
	}

	/**
	 * This method should be used to enforce security on a
	 * <code>MethodInvocation</code>.
	 *
	 * @param mi The method being invoked which requires a security decision
	 *
	 * @return The returned value from the method invocation (possibly modified by
	 *         the {@code AfterInvocationManager}).
	 *
	 * @throws Throwable if any error occurs
	 */
	public Object invoke(MethodInvocation mi) throws Throwable {
		InterceptorStatusToken token = super.beforeInvocation(mi);

		Method method = mi.getMethod();
		//得到该类下面的RequestMapping注解
    	PermissionAnnotation permissionAttr = method.getAnnotation(PermissionAnnotation.class);
        if (null != permissionAttr)
        {
        	System.out.println("-----IdSrvMethodSecurityInterceptor get permissionAttr: " + permissionAttr.PermissionName());
        }
        
		Object result;
		try {
			result = mi.proceed();
		} finally {
			super.finallyInvocation(token);
		}
		return super.afterInvocation(token, result);
	}

	public IdSrvSecurityMetadataSource getSecurityMetadataSource() {
		return this.securityMetadataSource;
	}

	public SecurityMetadataSource obtainSecurityMetadataSource() {
		return this.securityMetadataSource;
	}

	public void setSecurityMetadataSource(IdSrvSecurityMetadataSource newSource) {
		this.securityMetadataSource = newSource;
	}
}
