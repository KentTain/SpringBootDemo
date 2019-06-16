package com.example.repository;

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
import com.example.repository.IUserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {
	private Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class);

	@Autowired
	private IUserRepository userRepository;

	
	@Test
	public void testCrud() throws Exception {
		User newUser = new User("aa1", new Date(), 15000d);
		User dbUser = userRepository.save(newUser);
		Assert.assertEquals(true, dbUser != null);
		Assert.assertEquals(newUser.getUserName(), dbUser.getUserName());
		
		User user = userRepository.findByUserName("aa1");
		Assert.assertEquals(true, user != null);
		
		userRepository.delete(user);

	}

	@Test
	public void testExecuteUpdateSql() throws Exception {
		boolean success = userRepository.executeUpdateSql("update tb_user set `user_name` = 'test-aa-1-modified' where `user_id` = 1;", null);
		Assert.assertEquals(true, success);
	}
	
	@Test
	public void testSqlQuery() throws Exception {
		//List<User> result = userRepository.sqlQuery("SELECT user_id, user_name, user_birthday, user_salary FROM tb_user");
		List<User> result = userRepository.sqlQuery("FROM tb_user");
		Assert.assertEquals(true, result.size() > 0);
		
		logger.debug("--testSqlQuery: ");
	}
	
}