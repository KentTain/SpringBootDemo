package com.example.repository;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.model.SysSequence;
import com.example.model.TenantInfo;
import com.example.multitenancy.TenantContext;
import com.example.multitenancy.TenantDataSourceProvider;

@DataJpaTest
@RunWith(SpringRunner.class)
//@Sql(scripts = "/sequence-data.sql")
public class SysSequenceRepositoryTest {
	private Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class.getName());
	private static TenantInfo testTenant;
	private static TenantInfo devdbTenant;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		devdbTenant = TenantContext.GetTenantByTenantId(TenantContext.DEFAULT_TENANTID_DEVDB);
		testTenant = TenantContext.GetTenantByTenantId(TenantContext.DEFAULT_TENANTID_TEST);

		TenantDataSourceProvider.addDataSource(devdbTenant);
		TenantDataSourceProvider.addDataSource(testTenant);
	}

	@AfterClass
	public static void setDownAfterClass() throws Exception {
		TenantDataSourceProvider.clearDataSource();
	}

	@Autowired
	private ISysSequenceRepository sysSequenceRepository;
	
	@Test
	public void test_sysSequence_devdb_crud() {
		TenantContext.setCurrentTenant(devdbTenant.getUsername());
		logger.info(String.format("-----Tenant: %s test----", TenantContext.getCurrentTenant()));

		SysSequence newUser = SysSequence.builder().sequenceName("TestSeq").initValue(1).maxValue(10000).currentValue(1)
				.stepValue(1).currDate("2019-01-01").preFixString("CRU").postFixString("End").comments("Test").build();
		SysSequence dbUser = sysSequenceRepository.saveAndFlush(newUser);
		Assert.assertEquals(true, dbUser != null);
		Assert.assertEquals(newUser.getSequenceName(), dbUser.getSequenceName());

		boolean isExists = sysSequenceRepository.existsById("TestSeq");
		Assert.assertEquals(true, isExists);
		
		SysSequence user = sysSequenceRepository.findByPreFixString("CRU");
		SysSequence user2 = sysSequenceRepository.findByPreFixString("End");

		sysSequenceRepository.delete(dbUser);
	}

}
