package com.example.multitenancy;


import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.model.TenantInfo;

/**
 * 指定了 ConnectionProvider，即 Hibernate 需要知道如何以租户特有的方式获取数据连接
 * 
 * @author tianchangjun
 * @version 1.0
 */
public class MsSqlMultiTenantConnectionProviderImpl
		extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
	private Logger logger = LoggerFactory.getLogger(MsSqlMultiTenantConnectionProviderImpl.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 返回默认的数据源
	 */
	protected DataSource selectAnyDataSource() {
		logger.debug("-----get default tenant: " + TenantContext.DEFAULT_TENANTID_DEVDB);
		return TenantDataSourceProvider.getTenantDataSource(TenantContext.DEFAULT_TENANTID_DEVDB);
	}

	@Autowired
	DataSource dataSource;

	/**
	 * 根据tenantIdentifier返回指定数据源
	 * 
	 * @param tenantIdentifier
	 * @return 数据源
	 */
	protected DataSource selectDataSource(String tenantIdentifier) {
		logger.debug("-----get tenant: " + tenantIdentifier);

		DataSource ds = TenantDataSourceProvider.getTenantDataSource(tenantIdentifier);
		if (ds != null)
			return ds;
		
		TenantInfo tenant = com.example.multitenancy.TenantContext.GetTenantByTenantId(tenantIdentifier);
		if(tenant == null)
			throw new RuntimeException(String.format("未找到相关租户: %s 的数据源", tenantIdentifier));
		
		TenantDataSourceProvider.addDataSource(tenant);
		
		return TenantDataSourceProvider.getTenantDataSource(tenantIdentifier);
	}
}