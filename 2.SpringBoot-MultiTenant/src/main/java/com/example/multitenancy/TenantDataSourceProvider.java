package com.example.multitenancy;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;

import com.example.model.TenantInfo;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


/**
 * 根据租户ID来提供对应的数据源
 * 
 * @author tianchangjun
 * @version 1.0
 */
public class TenantDataSourceProvider {
	private static Logger logger = LoggerFactory.getLogger(TenantDataSourceProvider.class.getName());
	// 使用一个map来存储我们租户和对应的数据源，租户和数据源的信息就是从我们的tenant_info表中读出来
	private static Map<String, DataSource> dataSourceMap = new HashMap<>();

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
	public static DataSource getTenantDataSource(String tenantId) {
		if (dataSourceMap.containsKey(tenantId)) {
			logger.debug("-----GetDataSource:" + tenantId);
			return dataSourceMap.get(tenantId);
		} else {
			logger.debug("-----GetDataSource:" + DEFAULT_SCHEMA);
			return dataSourceMap.get("Default");
		}
	}

	// 初始化的时候用于添加数据源的方法
	public static void addDataSource(TenantInfo tenantInfo) {
		/* //com.mchange.v2.c3p0
		 * ComboPooledDataSource defaultDataSource = new
		 * ComboPooledDataSource(tenantInfo.getTenantId());
		 * defaultDataSource.setDataSourceName("sm_project");
		 * defaultDataSource.setJdbcUrl(tenantInfo.getUrl());
		 * defaultDataSource.setUser(tenantInfo.getUsername());
		 * defaultDataSource.setPassword(tenantInfo.getPassword());
		 * defaultDataSource.setMaxIdleTime(180000);
		 * defaultDataSource.setMinPoolSize(2); defaultDataSource.setMaxPoolSize(10);
		 * defaultDataSource.setInitialPoolSize(3);
		 * defaultDataSource.setMaxStatements(1000);
		 * defaultDataSource.setMaxConnectionAge(10000); try {
		 * defaultDataSource.setDriverClass(
		 * "com.microsoft.sqlserver.jdbc.SQLServerDriver"); } catch
		 * (PropertyVetoException e) { logger.error(e.getMessage()); }
		 * 
		 * dataSourceMap.put(tenantInfo.getTenantId(), defaultDataSource);
		 */
		
		//com.zaxxer.hikari
		HikariConfig config = new HikariConfig();
		config.setPoolName(tenantInfo.getTenantId());
		config.setSchema(tenantInfo.getTenantId());
		config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		config.setJdbcUrl(tenantInfo.getUrl());
		config.setUsername(tenantInfo.getUsername());
		config.setPassword(tenantInfo.getPassword());
		config.setConnectionTimeout(30000);
		config.setIdleTimeout(600000);
		config.setMaxLifetime(1800000);
		config.setMaximumPoolSize(10);
		config.setMinimumIdle(1000);
		config.addDataSourceProperty("dataSource.cachePrepStmts", "true");
		config.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
		config.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
		config.addDataSourceProperty("dataSource.useServerPrepStmts", "true");
		
		HikariDataSource ds = new HikariDataSource(config);

		dataSourceMap.put(tenantInfo.getTenantId(), ds);
	}

	// 清除数据源，并保留默认的数据源设置
	public static void clearDataSource() {
		dataSourceMap.clear();

		initDataSource();
	}

	private static void initDataSource() {
		/* //com.mchange.v2.c3p0
		 * ComboPooledDataSource defaultDataSource = new
		 * ComboPooledDataSource(DEFAULT_POOL_CONFIG);
		 * defaultDataSource.setDataSourceName("sm_project");
		 * defaultDataSource.setJdbcUrl(
		 * "jdbc:sqlserver://localhost;databaseName=sm_project");
		 * defaultDataSource.setUser(DEFAULT_SCHEMA);
		 * defaultDataSource.setPassword("P@ssw0rd");
		 * defaultDataSource.setMaxIdleTime(180000);
		 * defaultDataSource.setMinPoolSize(2); defaultDataSource.setMaxPoolSize(10);
		 * defaultDataSource.setInitialPoolSize(3);
		 * defaultDataSource.setMaxStatements(1000);
		 * defaultDataSource.setMaxConnectionAge(10000); try {
		 * defaultDataSource.setDriverClass(
		 * "com.microsoft.sqlserver.jdbc.SQLServerDriver"); } catch
		 * (PropertyVetoException e) { logger.error(e.getMessage()); }
		 * 
		 * dataSourceMap.put(DEFAULT_SCHEMA, defaultDataSource);
		 */
		
		//com.zaxxer.hikari
		HikariConfig config = new HikariConfig();
		config.setPoolName(DEFAULT_POOL_CONFIG);
		config.setSchema(DEFAULT_SCHEMA);
		config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		config.setJdbcUrl("jdbc:sqlserver://localhost;databaseName=sm_project");
		config.setUsername(DEFAULT_SCHEMA);
		config.setPassword("P@ssw0rd");
		config.setConnectionTimeout(30000);
		config.setIdleTimeout(600000);
		config.setMaxLifetime(1800000);
		config.setMaximumPoolSize(10);
		config.setMinimumIdle(1000);
		config.addDataSourceProperty("dataSource.cachePrepStmts", "true");
		config.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
		config.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
		config.addDataSourceProperty("dataSource.useServerPrepStmts", "true");
		
		HikariDataSource ds = new HikariDataSource(config);

		dataSourceMap.put(DEFAULT_SCHEMA, ds);
	}

}