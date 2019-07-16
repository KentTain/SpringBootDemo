package com.example;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import com.example.annotation.AnnotationUtil;

public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent>{
	private final String pacakgeName;
	public ApplicationStartedEventListener(String packageName)
	{
		this.pacakgeName = packageName;
	}
	
	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		AnnotationUtil.initPermissionDataByPackageName(pacakgeName);
	}

}
