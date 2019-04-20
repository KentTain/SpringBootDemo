package com.example;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC配置
 * 
 * @author tianchangjun
 * @version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Resource
	private TenantInterceptor accessSignAuthInterceptor;

	/**
	 * Description : Group :
	 * <p>
	 * 实现自定义拦截器只需要3步 1、创建我们自己的拦截器类并实现 HandlerInterceptor 接口。
	 * 2、创建一个Java类继承WebMvcConfigurerAdapter，并重写 addInterceptors 方法。
	 * 3、实例化我们自定义的拦截器，然后将对像手动添加到拦截器链中（在addInterceptors方法中添加）。
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		InterceptorRegistration registration = registry.addInterceptor(accessSignAuthInterceptor);
		// 拦截配置
		registration.addPathPatterns("/api/**");
		// 排除配置
		registration.excludePathPatterns("/api/word");
	}
}