package com.example.service;

import com.example.model.User;

public interface IUserService {

	void deleteById(Long userId);

	User insert(User user);

	User findById(Long userId);

	User findByName(String userName);

}