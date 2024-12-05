package com.example.cognito_springsecurity.controller;

import com.example.cognito_springsecurity.dto.TokenDTO;
import com.example.cognito_springsecurity.dto.UrlDTO;
import com.example.cognito_springsecurity.service.CognitoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final CognitoService cognitoService;

	@Autowired
	public AuthController(CognitoService cognitoService){
		this.cognitoService = cognitoService;
	}

	@GetMapping("/url")
	public ResponseEntity<UrlDTO> getLoginUrl() {
		return ResponseEntity.ok().body(cognitoService.getLoginUrl());
	}

	@GetMapping("/callback")
	public ResponseEntity<TokenDTO> callback(@RequestParam("code") @Valid String code) {
		TokenDTO tokenDTO = cognitoService.getToken(code);
		return ResponseEntity.ok().body(tokenDTO);
	}
}
