package com.example.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.model.User;
import com.example.service.IUserService;

@Controller
@RequestMapping("/user")
public class UserController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private IUserService userService;

	// /user/{id}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView detail(@PathVariable String id, Model model) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		logger.debug(user.toString());
		model.addAttribute("user", user);
		return new ModelAndView("user/detail", "user", user);
	}

	// /userdata/{id}
	@RequestMapping(value = "/userdata/{id}", method = RequestMethod.GET)
	public @ResponseBody User getUserData(@PathVariable String id) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		logger.debug(user.toString());
		return user;
	}

	// /user/list
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		Iterable<User> users = this.userService.findAll();
		return new ModelAndView("user/list", "users", users);
	}

	// /usersdata
	@RequestMapping(value = "/usersdata", method = RequestMethod.GET)
	public @ResponseBody List<User> getUsersData() {
		List<User> users = this.userService.findAll();

		return users;
	}
}