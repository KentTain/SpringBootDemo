package com.example.multitenancy;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.model.TenantInfo;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import jodd.io.FileUtil;

/**
 * 根据租户ID来提供对应的数据源
 * 
 * @author tianchangjun
 * @version 1.0
 */
public class TenantDataSourceProvider {
	private static Logger logger = LoggerFactory.getLogger(TenantContext.class.getName());
	// 使用一个map来存储我们租户和对应的数据源，租户和数据源的信息就是从我们的tenant_info表中读出来
	private static Map<String, ComboPooledDataSource> dataSourceMap = new HashMap<>();

	public static final String DEFAULT_POOL_CONFIG = "default";
	public static final String DEFAULT_SCHEMA = "sa";

	/**
	 * 静态建立一个数据源，也就是我们的默认数据源，假如我们的访问信息里面没有指定tenantId，就使用默认数据源。
	 * 在我这里默认数据源是cloud_config，实际上你可以指向你们的公共信息的库，或者拦截这个操作返回错误。
	 */
	static {
		initDataSource();
	}

	// 根据传进来的tenantId决定返回的数据源
	public static ComboPooledDataSource getTenantDataSource(String tenantId) {
		if (dataSourceMap.containsKey(tenantId)) {
			logger.info("-----GetDataSource:" + tenantId);
			return dataSourceMap.get(tenantId);
		} else {
			logger.info("-----GetDataSource:" + DEFAULT_SCHEMA);
			return dataSourceMap.get("Default");
		}
	}

	// 初始化的时候用于添加数据源的方法
	public static void addDataSource(TenantInfo tenantInfo) {
		ComboPooledDataSource defaultDataSource = new ComboPooledDataSource(tenantInfo.getTenantId());
		defaultDataSource.setDataSourceName("sm_project");
		defaultDataSource.setJdbcUrl(tenantInfo.getUrl());
		defaultDataSource.setUser(tenantInfo.getUsername());
		defaultDataSource.setPassword(tenantInfo.getPassword());
		defaultDataSource.setMaxIdleTime(180000);
		defaultDataSource.setMinPoolSize(2);
		defaultDataSource.setMaxPoolSize(10);
		defaultDataSource.setInitialPoolSize(3);
		defaultDataSource.setMaxStatements(1000);
		defaultDataSource.setMaxConnectionAge(10000);

		try {
			defaultDataSource.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (PropertyVetoException e) {
			logger.error(e.getMessage());
		}

		dataSourceMap.put(tenantInfo.getTenantId(), defaultDataSource);
	}

	// 清除数据源，并保留默认的数据源设置
	public static void clearDataSource() {
		dataSourceMap.clear();

		initDataSource();
	}

	private static void initDataSource() {
		ComboPooledDataSource defaultDataSource = new ComboPooledDataSource(DEFAULT_POOL_CONFIG);
		defaultDataSource.setDataSourceName("sm_project");
		defaultDataSource.setJdbcUrl("jdbc:sqlserver://localhost;databaseName=sm_project");
		defaultDataSource.setUser(DEFAULT_SCHEMA);
		defaultDataSource.setPassword("P@ssw0rd");
		defaultDataSource.setMaxIdleTime(180000);
		defaultDataSource.setMinPoolSize(2);
		defaultDataSource.setMaxPoolSize(10);
		defaultDataSource.setInitialPoolSize(3);
		defaultDataSource.setMaxStatements(1000);
		defaultDataSource.setMaxConnectionAge(10000);

		try {
			defaultDataSource.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (PropertyVetoException e) {
			logger.error(e.getMessage());
		}

		dataSourceMap.put(DEFAULT_SCHEMA, defaultDataSource);
	}

	public static boolean createDataBases(TenantInfo tenantInfo) {
		try {
			String tenantId = tenantInfo.getTenantId();
			ComboPooledDataSource dateSource = getTenantDataSource(tenantId);
			JdbcTemplate template = getTemplate(dateSource);
			String sql = "CREATE DATABASE IF NOT EXISTS `" + tenantId
					+ "` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;";
			template.execute(sql);
			ComboPooledDataSource newDateSource = getTenantDataSource(tenantId);
			template.setDataSource(newDateSource);
			template.execute("use `" + tenantId + "`");
			template.batchUpdate(loadSql());
			logger.info("创建数据库[{}]成功", tenantId);
			return true;
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	private static String[] loadSql() {
		String[] result = new String[0];
		try {
			Resource resource = new ClassPathResource("news.sql");
			File resourceFile = resource.getFile();
			if (resourceFile.canRead()) {
				String[] lines = FileUtil.readLines(resourceFile, "UTF-8");
				List<String> sqls = Arrays.asList(lines);
				List<String> tmp = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				for (String sql : sqls) {
					sb.append(sql);
					if (sb.indexOf(";") != -1) {
						tmp.add(sb.toString());
						sb.delete(0, sb.length());
					}
				}
				return tmp.toArray(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	private static JdbcTemplate getTemplate(ComboPooledDataSource dataSource) {
		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(dataSource);
		return template;
	}

}