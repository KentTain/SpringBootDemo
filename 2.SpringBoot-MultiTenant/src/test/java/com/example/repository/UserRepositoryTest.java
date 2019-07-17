package com.example.repository;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.model.TenantInfo;
import com.example.model.User;
import com.example.multitenancy.TenantContext;
import com.example.multitenancy.TenantDataSourceProvider;

@RunWith(SpringRunner.class)
@SpringBootTest
//@EnableJpaRepositories(repositoryFactoryBeanClass = TreeNodeRepositoryFactoryBean.class)
public class UserRepositoryTest {
	private static TenantInfo testTenant;
	private static TenantInfo devdbTenant;
	private Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {
		devdbTenant = TenantContext.GetTenantByTenantId(TenantContext.DEFAULT_TENANTID_DEVDB);
		testTenant = TenantContext.GetTenantByTenantId(TenantContext.DEFAULT_TENANTID_TEST);

		TenantDataSourceProvider.addDataSource(devdbTenant);
		TenantDataSourceProvider.addDataSource(testTenant);
	}

	@AfterClass
	public static void setDown() throws Exception {
		TenantDataSourceProvider.clearDataSource();
	}

	@Autowired
	private IUserRepository userRepository;

	@Test
	public void test_user_devdb_crud() throws Exception {
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
	public void test_user_test_crud() throws Exception {
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