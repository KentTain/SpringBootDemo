package com.example.Interceptor;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.TenantInfo;

public class TenantContext {
	private static Logger logger = LoggerFactory.getLogger(TenantContext.class.getName());
	//private static ThreadLocal<TenantInfo> currentTenant = new ThreadLocal<TenantInfo>();
	private static TenantInfo currentTenant = new TenantInfo();
	private static Set<TenantInfo> Tenants = new HashSet<TenantInfo>();

	public static final String DEFAULT_TENANTID_DEVDB = "cdba";
	public static final String DEFAULT_TENANTID_TEST = "ctest";
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
		devdbTenant.setDomain(DEFAULT_TENANTID_DEVDB + ".localhost");
		devdbTenant.setClientId("Y0RiYQ==");
		devdbTenant.setClientSecret("MmJmNWIyM2Q5ZjY4OWU5YzFmYWVkZTUwNzY2ZWJkNTg=");
		
		Tenants.add(devdbTenant);

		TenantInfo testTenant = new TenantInfo();
		testTenant.setId(2);
		testTenant.setTenantId(DEFAULT_TENANTID_TEST);
		testTenant.setUrl(DEFAULT_JDBC_URL);
		testTenant.setUsername(DEFAULT_TENANTID_TEST);
		testTenant.setPassword(DEFAULT_TENANT_PASSWORD);
		testTenant.setDomain(DEFAULT_TENANTID_TEST + ".localhost");
		testTenant.setClientId("Y1Rlc3Q=");
		testTenant.setClientSecret("MmMyZjg3YTIzNzZkY2NhNjNlNDg0MGE4NGU4NzQ3YzM=");
		
		Tenants.add(testTenant);
	}

	public static void setCurrentTenant(TenantInfo tenant) {
		System.out.println("-----Setting tenant to " + tenant.getTenantId());
		currentTenant = tenant;
		//currentTenant.set(tenant);
	}

	public static TenantInfo getCurrentTenant() {
		return currentTenant;
		//return currentTenant.get();
	}
	public static void clear() {
		currentTenant = null;
		//currentTenant.set(null);
	}

	public static TenantInfo GetTenantByTenantId(String tenantName) {
		return Tenants.stream().filter(t -> tenantName.equalsIgnoreCase(t.getTenantId())).findFirst().get();
	}

	public static TenantInfo GetTenantByDomain(String domain) {
		return Tenants.stream().filter(t -> domain.equalsIgnoreCase(t.getDomain())).findFirst().get();
	}
}