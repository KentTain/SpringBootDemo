package com.example.Interceptor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**
 * MVC配置
 * 
 * @author tianchangjun
 * @version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	private Logger logger = LoggerFactory.getLogger(TenantInterceptor.class);

	/**
	 * 添加类型转换器和格式化器
	 * 
	 * @param registry
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		// registry.addFormatterForFieldType(LocalDate.class, new
		// USLocalDateFormatter());
	}

	/**
	 * 跨域支持
	 * 
	 * @param registry
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowCredentials(true)
				.allowedMethods("GET", "POST", "DELETE", "PUT").maxAge(3600 * 24);

		logger.info("-----WebMvcConfig registry Cors-----");
	}

	/**
	 * 添加静态资源--过滤swagger-api (开源的在线API文档)
	 * 
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 过滤swagger
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

		registry.addResourceHandler("/swagger-resources/**")
				.addResourceLocations("classpath:/META-INF/resources/swagger-resources/");

		registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/META-INF/resources/swagger*");

		registry.addResourceHandler("/v2/api-docs/**")
				.addResourceLocations("classpath:/META-INF/resources/v2/api-docs/");

		logger.info("-----WebMvcConfig addResourceHandlers-----");
	}

	/**
	 * 配置消息转换器--这里我用的是alibaba 开源的 fastjson
	 * 
	 * @param converters
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// 1.需要定义一个convert转换消息的对象;
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		// 2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteDateUseDateFormat);
		// 3处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		// 4.在convert中添加配置信息.
		fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
		// 5.将convert添加到converters当中.
		converters.add(fastJsonHttpMessageConverter);

		logger.info("-----WebMvcConfig configureMessageConverters FastJson-----");
	}

	/**
	 * 实现自定义拦截器只需要3步: 1、创建我们自己的拦截器类并实现 HandlerInterceptor 接口。
	 * 2、创建一个Java类继承WebMvcConfigurer，并重写 addInterceptors 方法。
	 * 3、实例化我们自定义的拦截器，然后将对像手动添加到拦截器链中（在addInterceptors方法中添加）。
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration registration = registry.addInterceptor(new TenantInterceptor());
		// 拦截配置l
		registration.addPathPatterns("/**");
		// 排除配置
		// registration.excludePathPatterns("/hello");

		logger.info("-----WebMvcConfig addInterceptors tenantInterceptor-----");
	}
}