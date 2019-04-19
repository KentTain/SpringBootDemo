package com.example.multitenancy;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.PooledDataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;

import javax.sql.DataSource;

/**
 * TenantDemo
 * 指定了 ConnectionProvider，即 Hibernate 需要知道如何以租户特有的方式获取数据连接
 *
 * @author 张浩伟
 * @version 1.01 2018年02月09日
 */
public class MsSqlDatabaseMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 返回默认的数据源
     */
    protected DataSource selectAnyDataSource() {
        return TenantDataSourceProvider.getTenantDataSource(TenantDataSourceProvider.DEFAULT_SCHEMA);
    }

    /**
     * 根据tenantIdentifier返回指定数据源
     * @param tenantIdentifier
     * @return
     */
    protected DataSource selectDataSource(String tenantIdentifier) {
        PooledDataSource dataSource = C3P0Registry.pooledDataSourceByName(tenantIdentifier);
        if (dataSource == null) {
        	DataSource ds = TenantDataSourceProvider.getTenantDataSource(tenantIdentifier);
        	if (ds != null)
        		return ds;
        	
        	return TenantDataSourceProvider.getTenantDataSource(TenantDataSourceProvider.DEFAULT_SCHEMA);
        }
        return dataSource;
    }
}
