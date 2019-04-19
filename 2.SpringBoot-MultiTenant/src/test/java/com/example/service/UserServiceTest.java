package com.example.service;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.model.TenantInfo;
import com.example.model.User;
import com.example.multitenancy.TenantContext;
import com.example.multitenancy.TenantDataSourceProvider;
import com.example.repository.UserRepositoryTests;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
	private TenantInfo testTenant = new TenantInfo();
	private TenantInfo devdbTenant = new TenantInfo();
	private Logger logger = LoggerFactory.getLogger(UserRepositoryTests.class.getName());
	
	@Before 
	public void setUp() throws Exception {
		devdbTenant.setId(1);
		devdbTenant.setTenantId("devdb");
		devdbTenant.setUrl("jdbc:sqlserver://localhost;databaseName=sm_project;");
		devdbTenant.setUsername("devdb");
		devdbTenant.setPassword("P@ssw0rd");
		
		testTenant.setId(2);
		testTenant.setTenantId("test");
		testTenant.setUrl("jdbc:sqlserver://localhost;databaseName=sm_project;");
		testTenant.setUsername("test");
		testTenant.setPassword("P@ssw0rd");
		
		TenantDataSourceProvider.addDataSource(devdbTenant);
		TenantDataSourceProvider.addDataSource(testTenant);
    }
	
	@After 
	public void setDown() throws Exception {
		TenantDataSourceProvider.clearDataSource();
    }
	
	@Autowired
	private IUserService userService;  
	
	@Test
	public void test_devdb_tenant() throws Exception {
		TenantContext.setCurrentTenant(devdbTenant.getUsername());
		logger.info(String.format("-----Tenant: %s test----", TenantContext.getCurrentTenant()));
		
		User newUser = new User("devdb-aa1", new Date(), 15000d);
		User dbUser = userService.insert(newUser);
		Assert.assertEquals(true, dbUser != null);
		Assert.assertEquals(newUser.getUserName(), dbUser.getUserName());
		
		User user = userService.findByName("devdb-aa1");
		Assert.assertEquals(true, user != null);
		
		userService.deleteById(user.getUserId());
	}
	
	@Test
	public void test_test_tenant() throws Exception {
		TenantContext.setCurrentTenant(testTenant.getUsername());
		logger.info(String.format("-----Tenant: %s test----", TenantContext.getCurrentTenant()));
		
		User newUser = new User("test-aa1", new Date(), 15000d);
		User dbUser = userService.insert(newUser);
		Assert.assertEquals(true, dbUser != null);
		Assert.assertEquals(newUser.getUserName(), dbUser.getUserName());
		
		User user = userService.findByName("test-aa1");
		Assert.assertEquals(true, user != null);
		
		userService.deleteById(user.getUserId());
	}
	
}
