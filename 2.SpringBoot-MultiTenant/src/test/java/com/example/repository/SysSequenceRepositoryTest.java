package com.example.repository;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.model.SysSequence;
import com.example.model.TenantInfo;
import com.example.multitenancy.TenantContext;
import com.example.multitenancy.TenantDataSourceProvider;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SysSequenceRepositoryTest {
	private Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class.getName());
	private static TenantInfo testTenant;
	private static TenantInfo devdbTenant;

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
	private ISysSequenceRepository sysSequenceRepository;

	@Test
	public void test_sysSequence_devdb_crud() {
		TenantContext.setCurrentTenant(devdbTenant.getUsername());
		logger.info(String.format("-----Tenant: %s test----", TenantContext.getCurrentTenant()));

		SysSequence newUser = SysSequence.builder().SequencName("TestSeq").InitValue(1).MaxValue(10000).CurrentValue(1)
				.StepValue(1).CurrDate("2019-01-01").PreFixString("CRU").Comments("Test").build();
		SysSequence dbUser = sysSequenceRepository.saveAndFlush(newUser);
		Assert.assertEquals(true, dbUser != null);
		Assert.assertEquals(newUser.getSequencName(), dbUser.getSequencName());

		boolean isExists = sysSequenceRepository.existsById("TestSeq");
		Assert.assertEquals(true, isExists);
		
		SysSequence user = sysSequenceRepository.findByPreFixString("CRU");

		sysSequenceRepository.delete(dbUser);
	}

}
