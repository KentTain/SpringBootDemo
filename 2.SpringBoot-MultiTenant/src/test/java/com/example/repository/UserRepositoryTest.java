package com.example.repository;

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

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {
	private TenantInfo testTenant;
	private TenantInfo devdbTenant;
	private Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class.getName());

	@Before
	public void setUp() throws Exception {
		devdbTenant = TenantContext.GetTenantByTenantId(TenantContext.DEFAULT_TENANTID_DEVDB);
		testTenant = TenantContext.GetTenantByTenantId(TenantContext.DEFAULT_TENANTID_TEST);

		TenantDataSourceProvider.addDataSource(devdbTenant);
		TenantDataSourceProvider.addDataSource(testTenant);
	}

	@After
	public void setDown() throws Exception {
		TenantDataSourceProvider.clearDataSource();
	}

	@Autowired
	private IUserRepository userRepository;

	@Test
	public void test_devdb_tenant() throws Exception {
		TenantContext.setCurrentTenant(devdbTenant.getUsername());
		logger.info(String.format("-----Tenant: %s test----", TenantContext.getCurrentTenant()));

		User newUser = new User("devdb-aa1", new Date(), 15000d);
		User dbUser = userRepository.save(newUser);
		Assert.assertEquals(true, dbUser != null);
		Assert.assertEquals(newUser.getUserName(), dbUser.getUserName());

		User user = userRepository.findByUserName("devdb-aa1");
		Assert.assertEquals(true, user != null);

		userRepository.delete(user);
	}

	@Test
	public void test_test_tenant() throws Exception {
		TenantContext.setCurrentTenant(testTenant.getUsername());
		logger.info(String.format("-----Tenant: %s test----", TenantContext.getCurrentTenant()));

		User newUser = new User("test-aa1", new Date(), 15000d);
		User dbUser = userRepository.save(newUser);
		Assert.assertEquals(true, dbUser != null);
		Assert.assertEquals(newUser.getUserName(), dbUser.getUserName());

		User user = userRepository.findByUserName("test-aa1");
		Assert.assertEquals(true, user != null);

		userRepository.delete(user);
	}

}