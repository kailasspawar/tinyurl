package com.convert.tinyurl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.convert.tinyurl.dao.UserRepository;
import com.convert.tinyurl.model.User;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;

	public boolean isValidUser(String username, String password) {
		 User user = userRepository.findByUsername(username);
	     return user != null && user.getPassword().equals(password);
	}
}
