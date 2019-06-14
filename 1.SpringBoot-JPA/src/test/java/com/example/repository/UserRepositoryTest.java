package com.example.repository;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.model.User;
import com.example.repository.IUserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

	@Autowired
	private IUserRepository userRepository;

	@Test
	public void testExecuteSql() throws Exception {
		boolean success = userRepository.executeSql("select * from tb_user", null);
		Assert.assertEquals(true, success);
	}
	
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

}