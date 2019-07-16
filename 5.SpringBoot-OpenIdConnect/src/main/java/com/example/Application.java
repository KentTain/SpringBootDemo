package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

import com.example.annotation.AnnotationUtil;

@SpringBootApplication
//@ComponentScan("com.example, com.example.*")
//@ServletComponentScan(basePackages = "com.example.RequestListenter")
public class Application {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
        app.addListeners(new ApplicationStartedEventListener("com.example.Controller"));//加入自定义的监听类
        app.run(args);
	}

}
