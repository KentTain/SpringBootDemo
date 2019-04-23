package com.example.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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

	// 自定义类型转换器
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		logger.info("------initBinder &&&&" + request.getParameter("userBirthday") + "***" + request.getParameter("username"));
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"), true));
	}
	
	@Autowired
	private IUserService userService;

	@GetMapping("/")
	public String index() {
		return "redirect:/user/list";
	}

	// /user/list
	@GetMapping("/list")
	public String list(Model model) {
		List<User> users = this.userService.findAll();
		model.addAttribute("users", users);
		return "user/list";
	}

	// /usersdata
	@GetMapping("/usersdata")
	public @ResponseBody List<User> getUsersData() {
		List<User> users = this.userService.findAll();
		return users;
	}

	// /user/detail/{id}
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable String id, Model model) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		model.addAttribute("user", user);
		//return new ModelAndView("user/detail", "user", user);
		return "user/detail";
	}

	// /userdata/{id}
	@GetMapping("/userdata/{id}")
	public @ResponseBody User getUserData(@PathVariable String id) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		return user;
	}

	// /user/toAdd
	@GetMapping("/toAdd")
	public String toAdd() {
		return "user/add";
	}

	@RequestMapping("/add")
	public String add(User user) {
		userService.save(user);
		return "redirect:/user/list";
	}
	
	// /user/toEdit?id={id}
	@GetMapping("/toEdit")
	public String toEdit(Long id, Model model) {
		User user = userService.findById(id);
		model.addAttribute("user", user);
		return "user/edit";
	}

	@RequestMapping("/edit")
	public String edit(User user) {
		userService.save(user);
		return "redirect:/user/list";
	}

	// /user/delete/{id}
	@GetMapping("/delete")
	public String delete(Long id) {
		userService.deleteById(id);
		return "redirect:/user/list";
	}
}