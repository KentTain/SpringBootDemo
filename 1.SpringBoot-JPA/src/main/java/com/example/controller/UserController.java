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

	@RequestMapping("/")
	public String index() {
		return "redirect:user/list";
	}

	// /user/list
	@RequestMapping("/list")
	public String list(Model model) {
		List<User> users = this.userService.findAll();
		model.addAttribute("users", users);
		return "user/list";
	}

	// /usersdata
	@RequestMapping("/usersdata")
	public @ResponseBody List<User> getUsersData() {
		List<User> users = this.userService.findAll();
		return users;
	}

	// /user/detail/{id}
	@RequestMapping("/detail/{id}")
	public ModelAndView detail(@PathVariable String id, Model model) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		logger.debug(user.toString());
		model.addAttribute("user", user);
		return new ModelAndView("user/detail", "user", user);
	}

	// /userdata/{id}
	@RequestMapping("/userdata/{id}")
	public @ResponseBody User getUserData(@PathVariable String id) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		return user;
	}

	// /user/toAdd
	@RequestMapping("/toAdd")
	public String toAdd() {
		return "user/add";
	}

	@RequestMapping("/add")
	public String add(User user) {
		userService.save(user);
		return "redirect:/list";
	}
	
	// /user/toEdit/{id}
	@RequestMapping("/toEdit/{id}")
	public String toEdit(Model model, Long id) {
		User user = userService.findById(id);
		model.addAttribute("user", user);
		return "user/edit";
	}

	@RequestMapping("/edit")
	public String edit(User user) {
		userService.save(user);
		return "redirect:/list";
	}

	// /user/delete/{id}
	@RequestMapping("/delete/{id}")
	public String delete(Long id) {
		userService.deleteById(id);
		return "redirect:/list";
	}
}