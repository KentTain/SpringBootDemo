package com.example.repository;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.model.ConfigAttribute;
import com.example.model.ConfigEntity;
import com.example.model.TenantInfo;
import com.example.multitenancy.TenantContext;
import com.example.multitenancy.TenantDataSourceProvider;

@RunWith(SpringRunner.class)
@SpringBootTest
//@DataJpaTest
//@Sql(scripts = "/config-data.sql")
public class ConfigRepositoryTest {
	private Logger logger = LoggerFactory.getLogger(ConfigRepositoryTest.class.getName());
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
    private IConfigAttributeRepository configAttributeRepository;
     
    @Autowired
    private IConfigEntityRepository configEntityRepository;
    
    @Test
    public void test_cDba_ConfigEnity_findByConfigName() {
    	TenantContext.setCurrentTenant(devdbTenant.getUsername());
    	ConfigEntity configEntiy = configEntityRepository.findByConfigName("cDba-汇潮支付");
    	Assert.assertEquals(configEntiy.getConfigName(), "cDba-汇潮支付");
    	
    	Set<ConfigAttribute> items = configEntiy.getConfigAttributes();
    	Assert.assertTrue(items.size() > 0);
    	
    }
    
    @Test
    public void test_cDba_ConfigAttribute_findByName() {
    	TenantContext.setCurrentTenant(devdbTenant.getUsername());
        ConfigAttribute item = configAttributeRepository.findByName("AdminAdviceURL");
        Assert.assertEquals(item.getName(), "AdminAdviceURL");
        
        ConfigEntity entity = item.getConfigEntity();
        Assert.assertTrue(entity != null);
    }
     
    

}
