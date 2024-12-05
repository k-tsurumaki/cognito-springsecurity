package com.example.cognito_springsecurity.service;

import com.example.cognito_springsecurity.dto.UserDTO;
import com.example.cognito_springsecurity.entity.User;
import com.example.cognito_springsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Long createUser(String username, String email) {
		User user = User.builder()
				.username(username)
				.email(email)
				.build();
		user = userRepository.save(user);
		return user.getUserId();
	}

	public List<UserDTO> getAllUsers() {
		List<User> users = userRepository.findByDeletedFlgFalse();
		List<UserDTO> userDTOs = new ArrayList<>();
		for (User user: users) {
			UserDTO userDTO = UserDTO.builder()
					.userId(user.getUserId())
					.username(user.getUsername())
					.email(user.getEmail())
					.role(user.getRole())
					.build();
			userDTOs.add(userDTO);
		}
		return userDTOs;
	}
}
