package com.example.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionAnnotation {

	/**
	 * 页面名称，即所属菜单的名称
	 * 
	 * @return String
	 */
	String MenuName() default "";

	/**
	 * 页面操作名称
	 * 
	 * @return String
	 */
	String PermissionName() default "";

	/**
	 * 访问资源地址，形如：/AreaName/ControllerName/ActionName?QueryString=xxx
	 * 
	 * @return String
	 */
	String Url() default "";

	/**
	 * 排序
	 * 
	 * @return int
	 */
	int Order() default 0;

	/**
	 * 是否为扩展页面
	 * 
	 * @return boolean
	 */
	boolean IsPage() default false;

	/**
	 * 菜单默认分配的角色
	 * 
	 * @return String
	 */
	String DefaultRoleId() default "";

	/**
	 * 访问资源地址，形如：http://domain/AreaName/ControllerName/ActionName?QueryString=xxx
	 * 
	 * @return
	 */
	//String FullUrl() default "";

	/**
	 * 菜单的权限控制Id
	 * 
	 * @return
	 */
	String AuthorityId() default "";
}
