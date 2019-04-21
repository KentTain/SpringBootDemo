package com.example.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.User;
import com.example.service.IUserService;

@RestController
public class HelloController {
	@Autowired
	private IUserService userService;

	@RequestMapping("/hello")
	public String hello(Locale locale, Model model) {
		return "Hello World";
	}

	// /user?id=1
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public User toIndex(HttpServletRequest request, Model model) {
		Long userId = Long.parseLong(request.getParameter("id"));
		User user = this.userService.findById(userId);
		model.addAttribute("user", user);
		return user;
	}
}