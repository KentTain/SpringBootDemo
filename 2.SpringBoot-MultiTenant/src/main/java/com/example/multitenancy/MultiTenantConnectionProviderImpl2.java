package com.example.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import javax.sql.DataSource;

/**
 * 这个类是Hibernate框架拦截sql语句并在执行sql语句之前更换数据源提供的类
 * @author lanyuanxiaoyao
 * @version 1.0
 */
public class MultiTenantConnectionProviderImpl2 extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl 
{

	@Override
	protected DataSource selectAnyDataSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DataSource selectDataSource(String tenantIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}
}
