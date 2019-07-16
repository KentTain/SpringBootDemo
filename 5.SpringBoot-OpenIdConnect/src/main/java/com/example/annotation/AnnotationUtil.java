package com.example.annotation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.web.servlet.mvc.Controller;

import com.example.ListExtensions;

public class AnnotationUtil {

	/**
	 * 获取某个包下面的PermissionAnnotation并保存至PermissionData中
	 * 
	 * @param packageName 包名称
	 */
	public static void initPermissionDataByPackageName(String packageName) {
		Reflections reflections = new Reflections(packageName, new MethodAnnotationsScanner());
		Set<Method> methods = reflections.getMethodsAnnotatedWith(PermissionAnnotation.class);
		List<PermissionAnnotation> permissionAttrData = new ArrayList<PermissionAnnotation>();
		for (Method method : methods) {
			PermissionAnnotation permissionAttr = method.getAnnotation(PermissionAnnotation.class);
			if (null != permissionAttr) {
				permissionAttrData.add(permissionAttr);
			}
		}

		// 父节点
		for (PermissionAnnotation item : permissionAttrData.stream().filter(m -> m.IsPage())
				.collect(Collectors.toList())) {
			PermissionData.AddResource(item, null);
		}

		// 子节点
		for (PermissionAnnotation item : permissionAttrData.stream().filter(m -> !m.IsPage())
				.collect(Collectors.toList())) {
			Optional<PermissionDTO> parentItem = PermissionData.AllPermissions.stream()
					.filter(m -> m.text.equals(item.MenuName() + "-" + item.MenuName())).findFirst();
			if (parentItem.isPresent())
				PermissionData.AddResource(item, parentItem.get());
		}
		System.out.println("-----AnnotationUtil get PermissionData: "
				+ ListExtensions.toCommaSeparatedStringByFilter(PermissionData.AllPermissions, m -> m.text));

	}
}
