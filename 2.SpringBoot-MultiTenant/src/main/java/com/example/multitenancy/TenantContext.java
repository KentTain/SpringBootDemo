package com.example.multitenancy;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.TenantInfo;

import jodd.util.StringUtil;

public class TenantContext {
	private static Logger logger = LoggerFactory.getLogger(TenantContext.class.getName());
	private static ThreadLocal<String> currentTenant = new ThreadLocal<>();
	private static Set<TenantInfo> Tenants = new HashSet<TenantInfo>();

	public static final String DEFAULT_TENANTID_DEVDB = "devdb";
	public static final String DEFAULT_TENANTID_TEST = "test";
	public static final String DEFAULT_TENANT_PASSWORD = "P@ssw0rd";
	public static final String DEFAULT_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DEFAULT_JDBC_URL="jdbc:sqlserver://127.0.0.1;databaseName=sm_project";
	
	static {
		TenantInfo devdbTenant = new TenantInfo();
		devdbTenant.setId(1);
		devdbTenant.setTenantId(DEFAULT_TENANTID_DEVDB);
		devdbTenant.setUrl(DEFAULT_JDBC_URL);
		devdbTenant.setUsername(DEFAULT_TENANTID_DEVDB);
		devdbTenant.setPassword(DEFAULT_TENANT_PASSWORD);
		devdbTenant.setDomain(DEFAULT_TENANTID_DEVDB + ".domain.com");
		Tenants.add(devdbTenant);

		TenantInfo testTenant = new TenantInfo();
		testTenant.setId(2);
		testTenant.setTenantId(DEFAULT_TENANTID_TEST);
		testTenant.setUrl(DEFAULT_JDBC_URL);
		testTenant.setUsername(DEFAULT_TENANTID_TEST);
		testTenant.setPassword(DEFAULT_TENANT_PASSWORD);
		testTenant.setDomain(DEFAULT_TENANTID_TEST + ".domain.com");
		Tenants.add(testTenant);
	}

	public static void setCurrentTenant(String tenant) {
		logger.debug("-----Setting tenant to " + tenant);
		currentTenant.set(tenant);
	}

	public static String getCurrentTenant() {
		return currentTenant.get();
	}

	public static void clear() {
		currentTenant.set(null);
	}

	public static TenantInfo GetTenantByTenantId(String tenantId) {
		return Tenants.parallelStream().filter(t -> StringUtil.equals(tenantId, t.getTenantId())).findFirst().get();
	}

	public static TenantInfo GetTenantByDomain(String domain) {
		return Tenants.parallelStream().filter(t -> StringUtil.equals(domain, t.getDomain())).findFirst().get();
	}
}