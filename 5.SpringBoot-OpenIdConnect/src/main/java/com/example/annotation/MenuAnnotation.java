package com.example.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuAnnotation {

	/**
	 * 页面名称，即所属菜单的名称
	 * 
	 * @return String
	 */
	String ParentMenuName() default "";

	/**
	 * 操作名称
	 * 
	 * @return String
	 */
	String MenuName() default "";

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
	boolean IsExtPage() default false;

	/**
	 * 菜单层级，分为三级（1,2,3）
	 * 
	 * @return int
	 */
	int Level() default 1;

	/**
	 * 小图标
	 * 
	 * @return String
	 */
	String SmallIcon() default "fa fa-bars";

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
	String FullUrl() default "";

	/**
	 * 菜单的权限控制Id
	 * 
	 * @return
	 */
	String AuthorityId() default "";
}
