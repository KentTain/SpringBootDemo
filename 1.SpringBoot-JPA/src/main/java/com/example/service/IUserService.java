package com.example.service;

import java.util.List;

import com.example.model.User;

public interface IUserService {

	List<User> findAll();
	
	User findById(Long userId);

	User findByName(String userName);
	
	void deleteById(Long userId);

	User save(User user);
	
	boolean executeSql(String query);
}