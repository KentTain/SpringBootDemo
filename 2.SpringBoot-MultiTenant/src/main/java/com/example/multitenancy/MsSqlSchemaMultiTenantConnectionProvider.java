package com.example.multitenancy;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsSqlSchemaMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider {

	private static Logger logger = LoggerFactory.getLogger(TenantContext.class.getName());
	private static final String DEFAULT_SCHEMA = "devdb";

	@Override
	protected ConnectionProvider getAnyConnectionProvider() {
		return initConnectionProvider(DEFAULT_SCHEMA, "P@ssw0rd");
	}

	@Override
	protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
		return initConnectionProvider(DEFAULT_SCHEMA, "P@ssw0rd");
	}

	private final String UserPreString = "U_";

	// for MySQL
	/*
	 * @Override public Connection getConnection(String tenantIdentifier) throws
	 * SQLException { Connection connection = super.getConnection(tenantIdentifier);
	 * connection.createStatement() .execute(String.format("use %s;",
	 * tenantIdentifier));
	 * 
	 * return connection; }
	 */

	// for Oracle
	/*
	 * @Override public Connection getConnection(String tenantIdentifier) throws
	 * SQLException { Connection connection = super.getConnection(tenantIdentifier);
	 * connection.createStatement()
	 * .execute(String.format("ALTER SESSION SET CURRENT_SCHEMA = %s",
	 * tenantIdentifier)); return connection; }
	 */

	// for PostgreSQL
	/*
	 * @Override public Connection getConnection(String tenantIdentifier) throws
	 * SQLException { Connection connection = super.getConnection(tenantIdentifier);
	 * connection.createStatement() .execute(String.format("SET search_path TO %s",
	 * tenantIdentifier)); return connection; }
	 */

	private ConnectionProvider initConnectionProvider(String userName, String password) {
		try {
			Properties properties = new Properties();

			properties.put(org.hibernate.cfg.AvailableSettings.URL, "jdbc:sqlserver://localhost;databaseName=sm_project;");
			properties.put(org.hibernate.cfg.AvailableSettings.USER, UserPreString + userName);
			properties.put(org.hibernate.cfg.AvailableSettings.PASS, password);
			properties.put(org.hibernate.cfg.AvailableSettings.DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
			properties.put(org.hibernate.cfg.AvailableSettings.DIALECT, "org.hibernate.dialect.SQLServer2012Dialect");
			properties.put(org.hibernate.cfg.AvailableSettings.DEFAULT_SCHEMA, userName);

			DriverManagerConnectionProviderImpl connectionProvider = new DriverManagerConnectionProviderImpl();

			connectionProvider.configure(properties);
			return connectionProvider;
		} catch (Exception e) {
			logger.error(e.getMessage());

			return null;
		}

	}
}
