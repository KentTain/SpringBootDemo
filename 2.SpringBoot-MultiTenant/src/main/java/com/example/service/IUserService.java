package com.example.service;

import java.util.List;

import com.example.model.User;

public interface IUserService {

	void deleteById(Long userId);

	User insert(User user);

	User findById(Long userId);

	User findByName(String userName);

	List<User> findAll();
}