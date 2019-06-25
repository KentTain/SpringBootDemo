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

import com.example.model.MenuNode;
import com.example.model.TenantInfo;
import com.example.multitenancy.TenantContext;
import com.example.multitenancy.TenantDataSourceProvider;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MenuRepositoryTest {
	private TenantInfo testTenant;
	private TenantInfo devdbTenant;
	private Logger logger = LoggerFactory.getLogger(MenuRepositoryTest.class.getName());

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
	private IMenuRepository menuRepository;
	
	@Test
	public void test_menu_devdb_Crud() throws Exception {
		TenantContext.setCurrentTenant(devdbTenant.getUsername());
		logger.info(String.format("-----Tenant: %s test----", TenantContext.getCurrentTenant()));

		MenuNode newMenu = new MenuNode();
		newMenu.setName("menu-1");
		newMenu.setParentNode(null);
		newMenu.setDesc("Menu one");
		newMenu.setLeaf(false);
		newMenu.setLevel(1);
		newMenu.setIndex(1);
		newMenu.setCreatedBy("admin");
		newMenu.setCreatedDate(new Date());
		newMenu.setModifiedBy("admin");
		newMenu.setModifiedDate(new Date());
		newMenu.setIsDeleted(false);
		MenuNode dbMenu = menuRepository.saveAndFlush(newMenu);
		Assert.assertEquals(true, dbMenu != null);
		Assert.assertEquals(newMenu.getName(), dbMenu.getName());

		MenuNode menu = menuRepository.findByName("devdb-aa1");
		Assert.assertEquals(true, menu != null);

		menuRepository.delete(menu);
	}


}
