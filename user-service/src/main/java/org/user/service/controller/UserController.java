package org.user.service.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.user.service.dto.CreateNewUser;
import org.user.service.dto.GetUserResponse;
import org.user.service.model.UserDetailsDB;
import org.user.service.service.userService;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class UserController {

	@Autowired
	userService userservice;
	
	@GetMapping("/user")
	public GetUserResponse getUserById() throws Exception {
		UserDetailsDB user = (UserDetailsDB) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		user = userservice.getUserById(user.getId());
		return GetUserResponse.builder().name(user.getName()).phone(user.getMobileNumber()).age(user.getAge()).password(user.getPassword()).email(user.getEmail()).createdOn(user.getCreatedDate()).updatedOn(user.getUpdatedon()).build();
	}
	
	@GetMapping(value = "/user/mobilenumber/{mobilenumber}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserDetailsDB getUserByMobile(@PathVariable("mobilenumber") String number) throws Exception {
		return userservice.getUserByMobile(number);
	}
	
	@PostMapping("/user/new")
	public void createUser(@RequestBody @Valid CreateNewUser createuser) throws JsonProcessingException {
		userservice.createUser(createuser.to());
	}
}
