package com.nishikatakagi.ProductDigital.mapper;

import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import com.nishikatakagi.ProductDigital.dto.UserRegisterRequestDto;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.service.SecurityService;

public class UserRegisterMapper {
	@Autowired
	SecurityService security;

	public UserRegisterMapper() {

	}

	public User toUser(UserRegisterRequestDto request) {
		Date currentDate = Date.valueOf(LocalDateTime.now().toLocalDate());
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(security.encode(request.getPassword()));
		user.setCreatedDate(currentDate);
		user.setPhone(request.getPhone());
		user.setFirstName(request.getFirstname());
		user.setLastName(request.getLastname());
		user.setRoleId(2);
		return user;
	}

}
