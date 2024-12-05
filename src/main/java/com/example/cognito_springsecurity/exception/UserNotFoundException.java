package com.example.cognito_springsecurity.exception;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(Long userId) {
		super("User ID:" + userId + "is not found.");
	}
}
