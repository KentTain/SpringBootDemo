package com.example;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
  *  定义创建数据源方法
 * @author tianchangjun
 * @version 1.0
 */
@Configuration // 定义配置信息类
public class DataSourceConfiguration {
	@Bean(name = "dataSource") // 定义Bean
	@Qualifier(value = "dataSource")
	@Primary // 主要的候选者
	@ConfigurationProperties(prefix = "c3p0") // 配置属性
	public DataSource dataSource() {
		return DataSourceBuilder.create().type(com.mchange.v2.c3p0.ComboPooledDataSource.class).build();
	}
}
