package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.User;
import com.example.service.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private IUserService userService;

	// /user/home
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home(HttpServletRequest request, Model model) {
		return "Hello World";
	}
	
	// /user/test?id=1
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(HttpServletRequest request, Model model) {
		Long userId = Long.parseLong(request.getParameter("id"));
		User user = null;
		if (userId == 1) {
			user = new User();
	        user.setUserBirthday(new Date());
	        user.setUserName("James");
	        user.setUserSalary(15000.0);
		}

		logger.debug(user.toString());
		model.addAttribute("user", user);
		return "index";
	}

	// /user/showUser?id=1
	@RequestMapping(value = "/showUser", method = RequestMethod.GET)
	public String toIndex(HttpServletRequest request, Model model) {
		Long userId = Long.parseLong(request.getParameter("id"));
		User user = this.userService.findById(userId);
		logger.debug(user.toString());
		model.addAttribute("user", user);
		return "showUser";
	}

	// /user/showUser2?id=1
	@RequestMapping(value = "/showUser2", method = RequestMethod.GET)
	public String toIndex2(@RequestParam("id") String id, Model model) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		logger.debug(user.toString());
		model.addAttribute("user", user);
		return "showUser";
	}

	// /user/showUser3/{id}
	@RequestMapping(value = "/showUser3/{id}", method = RequestMethod.GET)
	public String toIndex3(@PathVariable("id") String id, Map<String, Object> model) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		logger.debug(user.toString());
		model.put("user", user);
		return "showUser";
	}

	// /user/{id}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody User getUserInJson(@PathVariable String id, Map<String, Object> model) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		logger.debug(user.toString());
		return user;
	}

	// /user/jsontype/{id}
	@RequestMapping(value = "/jsontype/{id}", method = RequestMethod.GET)
	public ResponseEntity<User> getUserInJson2(@PathVariable String id, Map<String, Object> model) {
		Long userId = Long.parseLong(id);
		User user = this.userService.findById(userId);
		logger.debug(user.toString());
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	// /user/upload
	@RequestMapping(value = "/upload")
	public String showUploadPage() {
		return "user_admin/file";
	}

	@RequestMapping(value = "/doUpload", method = RequestMethod.POST)
	public String doUploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		if (!file.isEmpty()) {
			logger.debug("Process file:{}", file.getOriginalFilename());
		}
		FileUtils.copyInputStreamToFile(file.getInputStream(),
				new File("E:\\", System.currentTimeMillis() + file.getOriginalFilename()));
		return "succes";
	}
}
