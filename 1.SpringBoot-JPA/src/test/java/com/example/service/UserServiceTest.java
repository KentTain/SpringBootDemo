package com.example.service;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.model.User;
import com.example.repository.IDbRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
	private Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
	@Autowired
	private IUserService userService;  
	
	@Test
	public void testExecuteSql() throws Exception {
		boolean success = userService.executeSql("select * from t_user");
		Assert.assertEquals(true, success);
	}
	
	@Test
	public void testCrud() throws Exception {
		User newUser = new User("aa1", new Date(), 15000d);
		User dbUser = userService.save(newUser);
		Assert.assertEquals(true, dbUser != null);
		Assert.assertEquals(newUser.getUserName(), dbUser.getUserName());
		
		User user = userService.findByName("aa1");
		Assert.assertEquals(true, user != null);
		
		userService.deleteById(user.getUserId());
	}
	
	@Autowired
    private List<IDbRepository> repositories;
	
	@Test
	public void testGetAllRepositories(){
        for (IDbRepository<?, ?> baseRepository : repositories) {
            System.out.println("-----IDbRepository name: " + baseRepository.getClass().getName());
        }
    }

}
