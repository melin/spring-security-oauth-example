package com.github.melin.rest.controller;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.melin.rest.model.User;

@RestController
public class UserController {

	@RequestMapping(value="user.get", params={"version=1.0"})
	public User getUser(long id, Authentication authentication) {
		System.out.println("==" + authentication);
		
		//System.out.println("==" + SecurityContextHolder.getContext().getAuthentication());
		User user = new User();
		user.setId(id);
		user.setUsername("zhangsan");
		user.setAge(21);
		user.setAddress("地球");
		return user;
	}
	
	@RequestMapping(value="user.save", params={"version=1.0"})
	public void saveUser(@Valid User user) {
		System.out.println("user 保存成功 = " + user);
	}
	
}
