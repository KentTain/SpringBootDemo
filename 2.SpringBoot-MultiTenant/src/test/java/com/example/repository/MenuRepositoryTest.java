package com.example.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.spi.ValidationProvider;

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
	public void test_menu_devdb_crud() throws Exception {
		TenantContext.setCurrentTenant(devdbTenant.getUsername());
		logger.info(String.format("-----Tenant: %s test----", TenantContext.getCurrentTenant()));

		List<MenuNode> menus = InitMenus();
		
		printNode(menus.get(0), 3);
		printNode(menus.get(1), 2);
		
		List<MenuNode> dbMenus = menuRepository.saveAll(menus);
		Assert.assertEquals(true, dbMenus != null && dbMenus.size() > 0);
		Assert.assertEquals(menus.get(0).getName(), dbMenus.get(0).getName());

		MenuNode menu = menuRepository.findByName("menu-1");
		Assert.assertEquals(true, menu != null);

		//menuRepository.delete(menu);
	}
	
	@Test
	public void test_menu_test_crud() throws Exception {
		TenantContext.setCurrentTenant(testTenant.getUsername());
		logger.info(String.format("-----Tenant: %s test----", TenantContext.getCurrentTenant()));

		List<MenuNode> menus = InitMenus();
		
		printNode(menus.get(0), 3);
		printNode(menus.get(1), 2);
		
		List<MenuNode> dbMenus = menuRepository.saveAll(menus);
		Assert.assertEquals(true, dbMenus != null && dbMenus.size() > 0);
		Assert.assertEquals(menus.get(0).getName(), dbMenus.get(0).getName());

		MenuNode menu = menuRepository.findByName("menu-1");
		Assert.assertEquals(true, menu != null);

		//menuRepository.delete(menu);
	}

	/**
	 * 初始化菜单数据
	 * 
	 * @return
	 */
	private List<MenuNode> InitMenus() {
		List<MenuNode> resultList = new ArrayList<>();

		MenuNode rootNode0 = new MenuNode("root-1");
		rootNode0.setName("root-1");
		rootNode0.setParentNode(null);
		rootNode0.setLeaf(false);
		rootNode0.setLevel(0);
		rootNode0.setIndex(1);
		rootNode0.setCreatedBy("admin");
		rootNode0.setCreatedDate(new Date());
		rootNode0.setModifiedBy("admin");
		rootNode0.setModifiedDate(new Date());
		rootNode0.setIsDeleted(false);

		// 一级
		MenuNode node0 = new MenuNode("menu-1");
		node0.setName("menu-1");
		node0.setLeaf(false);
		node0.setLevel(1);
		node0.setIndex(1);
		node0.setCreatedBy("admin");
		node0.setCreatedDate(new Date());
		node0.setModifiedBy("admin");
		node0.setModifiedDate(new Date());
		node0.setIsDeleted(false);
		node0.setParentNode(rootNode0);
		rootNode0.getChildNodes().add(node0);

		// 二级
		MenuNode node0_0 = new MenuNode("menu-1-1");
		node0_0.setName("menu-1-1");
		node0_0.setLeaf(false);
		node0_0.setLevel(1);
		node0_0.setIndex(1);
		node0_0.setCreatedBy("admin");
		node0_0.setCreatedDate(new Date());
		node0_0.setModifiedBy("admin");
		node0_0.setModifiedDate(new Date());
		node0_0.setIsDeleted(false);
		node0_0.setParentNode(node0);
		node0.getChildNodes().add(node0_0);
		// 二级
		MenuNode node0_1 = new MenuNode("menu-1-2");
		node0_1.setName("menu-1-2");
		node0_1.setLeaf(false);
		node0_1.setLevel(2);
		node0_1.setIndex(1);
		node0_1.setCreatedBy("admin");
		node0_1.setCreatedDate(new Date());
		node0_1.setModifiedBy("admin");
		node0_1.setModifiedDate(new Date());
		node0_1.setIsDeleted(false);
		node0_1.setParentNode(node0);
		node0.getChildNodes().add(node0_1);
		// 二级
		MenuNode node0_2 = new MenuNode("menu-1-3");
		node0_2.setName("menu-1-3");
		node0_2.setLeaf(false);
		node0_2.setLevel(2);
		node0_2.setIndex(2);
		node0_2.setCreatedBy("admin");
		node0_2.setCreatedDate(new Date());
		node0_2.setModifiedBy("admin");
		node0_2.setModifiedDate(new Date());
		node0_2.setIsDeleted(false);
		node0_2.setParentNode(node0);
		node0.getChildNodes().add(node0_2);
		// 二级
		MenuNode node0_3 = new MenuNode("menu-1-4");
		node0_3.setName("menu-1-4");
		node0_3.setLeaf(false);
		node0_3.setLevel(2);
		node0_3.setIndex(3);
		node0_3.setCreatedBy("admin");
		node0_3.setCreatedDate(new Date());
		node0_3.setModifiedBy("admin");
		node0_3.setModifiedDate(new Date());
		node0_3.setIsDeleted(false);
		node0_3.setParentNode(node0);
		node0.getChildNodes().add(node0_3);

		// 三级
		MenuNode node0_3_0 = new MenuNode("menu-1-4-1");
		node0_3_0.setName("menu-1-4-1");
		node0_3_0.setLeaf(true);
		node0_3_0.setLevel(3);
		node0_3_0.setIndex(1);
		node0_3_0.setCreatedBy("admin");
		node0_3_0.setCreatedDate(new Date());
		node0_3_0.setModifiedBy("admin");
		node0_3_0.setModifiedDate(new Date());
		node0_3_0.setIsDeleted(false);
		node0_3_0.setParentNode(node0_3);
		node0_3.getChildNodes().add(node0_3_0);
		// 三级
		MenuNode node0_3_1 = new MenuNode("menu-1-4-2");
		node0_3_1.setName("menu-1-4-2");
		node0_3_1.setLeaf(true);
		node0_3_1.setLevel(3);
		node0_3_1.setIndex(2);
		node0_3_1.setCreatedBy("admin");
		node0_3_1.setCreatedDate(new Date());
		node0_3_1.setModifiedBy("admin");
		node0_3_1.setModifiedDate(new Date());
		node0_3_1.setIsDeleted(false);
		node0_3_1.setParentNode(node0_3);
		node0_3.getChildNodes().add(node0_3_1);
		// 三级
		MenuNode node0_3_2 = new MenuNode("menu-1-4-3");
		node0_3_2.setName("menu-1-4-2");
		node0_3_2.setLeaf(true);
		node0_3_2.setLevel(3);
		node0_3_2.setIndex(2);
		node0_3_2.setCreatedBy("admin");
		node0_3_2.setCreatedDate(new Date());
		node0_3_2.setModifiedBy("admin");
		node0_3_2.setModifiedDate(new Date());
		node0_3_2.setIsDeleted(false);
		node0_3_2.setParentNode(node0_3);
		node0_3.getChildNodes().add(node0_3_2);

		// 一级
		MenuNode node1 = new MenuNode("menu-2");
		node1.setName("menu-2");
		node1.setLeaf(false);
		node1.setLevel(1);
		node1.setIndex(2);
		node1.setCreatedBy("admin");
		node1.setCreatedDate(new Date());
		node1.setModifiedBy("admin");
		node1.setModifiedDate(new Date());
		node1.setIsDeleted(false);
		node1.setParentNode(rootNode0);
		rootNode0.getChildNodes().add(node1);
		// 二级
		MenuNode node1_0 = new MenuNode("menu-2-1");
		node1_0.setName("menu-2-1");
		node1_0.setLeaf(true);
		node1_0.setLevel(2);
		node1_0.setIndex(1);
		node1_0.setCreatedBy("admin");
		node1_0.setCreatedDate(new Date());
		node1_0.setModifiedBy("admin");
		node1_0.setModifiedDate(new Date());
		node1_0.setIsDeleted(false);
		node1_0.setParentNode(node1);
		node1.getChildNodes().add(node1_0);
		// 二级
		MenuNode node1_1 = new MenuNode("menu-2-2");
		node1_1.setName("menu-2-2");
		node1_1.setLeaf(true);
		node1_1.setLevel(2);
		node1_1.setIndex(2);
		node1_1.setCreatedBy("admin");
		node1_1.setCreatedDate(new Date());
		node1_1.setModifiedBy("admin");
		node1_1.setModifiedDate(new Date());
		node1_1.setIsDeleted(false);
		node1_1.setParentNode(node1);
		node1.getChildNodes().add(node1_1);
		// 二级
		MenuNode node1_2 = new MenuNode("menu-2-3");
		node1_2.setName("menu-2-3");
		node1_2.setLeaf(true);
		node1_2.setLevel(2);
		node1_2.setIndex(3);
		node1_2.setCreatedBy("admin");
		node1_2.setCreatedDate(new Date());
		node1_2.setModifiedBy("admin");
		node1_2.setModifiedDate(new Date());
		node1_2.setIsDeleted(false);
		node1_2.setParentNode(node1);
		node1.getChildNodes().add(node1_2);

		resultList.add(rootNode0);

		MenuNode rootNode1 = new MenuNode("root-2");
		rootNode1.setName("root-2");
		rootNode1.setParentNode(null);
		rootNode1.setLeaf(false);
		rootNode1.setLevel(0);
		rootNode1.setIndex(1);
		rootNode1.setCreatedBy("admin");
		rootNode1.setCreatedDate(new Date());
		rootNode1.setModifiedBy("admin");
		rootNode1.setModifiedDate(new Date());
		rootNode1.setIsDeleted(false);

		// 一级
		MenuNode node01 = new MenuNode("menu-1");
		node01.setName("menu-1");
		node01.setLeaf(false);
		node01.setLevel(1);
		node01.setIndex(1);
		node01.setCreatedBy("admin");
		node01.setCreatedDate(new Date());
		node01.setModifiedBy("admin");
		node01.setModifiedDate(new Date());
		node01.setIsDeleted(false);
		node01.setParentNode(rootNode1);
		rootNode1.getChildNodes().add(node01);

		// 一级
		MenuNode node02 = new MenuNode("menu-2");
		node02.setName("menu-2");
		node02.setLeaf(false);
		node02.setLevel(1);
		node02.setIndex(2);
		node02.setCreatedBy("admin");
		node02.setCreatedDate(new Date());
		node02.setModifiedBy("admin");
		node02.setModifiedDate(new Date());
		node02.setIsDeleted(false);
		node02.setParentNode(rootNode1);
		rootNode1.getChildNodes().add(node02);

		resultList.add(rootNode1);

		return resultList;
	}

	private void printNode(MenuNode node, int level) {
		String preStr = "";
		for (int i = 0; i < level; i++) {
			preStr += "|----";
		}
		System.out.println(preStr + node.getName());
		for (MenuNode children : node.getChildNodes()) {
			printNode(children, level + 1);
		}
	}
}
