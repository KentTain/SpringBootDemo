package com.example.Interceptor;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.Tenant;

public class TenantContext {
	private static Logger logger = LoggerFactory.getLogger(TenantContext.class.getName());
	private static ThreadLocal<Tenant> currentTenant = new ThreadLocal<Tenant>();
	//private static Tenant currentTenant = new Tenant();
	private static Set<Tenant> Tenants = new HashSet<Tenant>();

	public static final String DEFAULT_TENANTID_DEVDB = "cdba";
	public static final String DEFAULT_TENANTID_TEST = "ctest";
	public static final String DEFAULT_TENANT_PASSWORD = "P@ssw0rd";
	public static final String DEFAULT_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DEFAULT_JDBC_URL="jdbc:sqlserver://127.0.0.1;databaseName=sm_project";
	
	static {
		Tenant devdbTenant = new Tenant();
		devdbTenant.setId(1);
		devdbTenant.setTenantName(DEFAULT_TENANTID_DEVDB);
		devdbTenant.setUrl(DEFAULT_JDBC_URL);
		devdbTenant.setUsername(DEFAULT_TENANTID_DEVDB);
		devdbTenant.setPassword(DEFAULT_TENANT_PASSWORD);
		devdbTenant.setDomain(DEFAULT_TENANTID_DEVDB + ".localhost");
		devdbTenant.setClientId("Y0RiYQ==");
		devdbTenant.setClientSecret("MmJmNWIyM2Q5ZjY4OWU5YzFmYWVkZTUwNzY2ZWJkNTg=");
		
		Tenants.add(devdbTenant);

		Tenant testTenant = new Tenant();
		testTenant.setId(2);
		testTenant.setTenantName(DEFAULT_TENANTID_TEST);
		testTenant.setUrl(DEFAULT_JDBC_URL);
		testTenant.setUsername(DEFAULT_TENANTID_TEST);
		testTenant.setPassword(DEFAULT_TENANT_PASSWORD);
		testTenant.setDomain(DEFAULT_TENANTID_TEST + ".localhost");
		testTenant.setClientId("Y1Rlc3Q=");
		testTenant.setClientSecret("MmMyZjg3YTIzNzZkY2NhNjNlNDg0MGE4NGU4NzQ3YzM=");
		
		Tenants.add(testTenant);
	}

	public static void setCurrentTenant(Tenant tenant) {
		//System.out.println("-----Setting tenant to " + tenant.getTenantName());
		//currentTenant = tenant;
		currentTenant.set(tenant);
	}

	public static Tenant getCurrentTenant() {
		//return currentTenant;
		return currentTenant.get();
	}
	public static void clear() {
		//currentTenant = null;
		currentTenant.set(null);
	}

	public static Tenant GetTenantByTenantId(String tenantName) {
		return Tenants.stream().filter(t -> tenantName.equalsIgnoreCase(t.getTenantName())).findFirst().get();
	}

	public static Tenant GetTenantByDomain(String domain) {
		//System.out.println("-----get tenant by domain: " + domain);
		return Tenants.stream().filter(t -> domain.equalsIgnoreCase(t.getDomain())).findFirst().get();
	}
}