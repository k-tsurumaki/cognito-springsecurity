package com.example.cognito_springsecurity.service;

import com.example.cognito_springsecurity.dto.TokenDTO;
import com.example.cognito_springsecurity.dto.UrlDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.Map;

@Service
public class CognitoService {

	@Value("${auth.cognitoUri}")
	private String cognitoUri;

	@Value("${spring.security.oauth2.resourceserver.jwt.clientId}")
	private String clientId;

	@Value("${spring.security.oauth2.resourceserver.jwt.clientSecret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.resourceserver.jwt.redirect-uri}")
	private String redirectUri;

	private final RestTemplate restTemplate;
	private final UserService userService;

	@Autowired
	public CognitoService(RestTemplate restTemplate, UserService userService) {
		this.restTemplate = restTemplate;
		this.userService = userService;
	}

	public TokenDTO getToken(@Valid String code) {
		String tokenEndpoint = cognitoUri + "/oauth2/token";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(clientId, clientSecret);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("code", code);
		params.add("redirect_uri", redirectUri);
		params.add("client_id", clientId);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<Map> response = restTemplate.exchange(
					tokenEndpoint,
					HttpMethod.POST,
					requestEntity,
					Map.class
			);

			Map<String, Object> responseBody = response.getBody();
			if (responseBody == null || !responseBody.containsKey("id_token")) {
				throw new IllegalStateException("ID token not found in response");
			}

			String idToken = (String) responseBody.get("id_token");
			String accessToken = (String) responseBody.get("access_token");

			Map<String, Object> userInfo = getUserInfo(accessToken);
			String email = (String) userInfo.get("email");
			String name = (String) userInfo.get("name");

			Long userId = userService.createUser(name, email); // userServiceを使用

			TokenDTO tokenDTO = TokenDTO.builder()
					.idToken(idToken)
					.accessToken(accessToken)
					.userId(userId)
					.build();
			return tokenDTO;

		} catch (Exception e) {
			throw new RuntimeException("Error fetching the ID token", e);
		}
	}

	// ユーザ情報はIDトークンにも含まれている。Cognitoの公開鍵を用いてIDトークンを自分でデコードして取得することもできるが今回はCognito側
	// で提供されているuserInfoエンドポイントを利用して取得する
	private Map<String, Object> getUserInfo(String accessToken) {
		String userInfoEndpoint = cognitoUri + "/oauth2/userInfo";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);

		HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		try {
			ResponseEntity<Map> response = restTemplate.exchange(
					userInfoEndpoint,
					HttpMethod.GET,
					requestEntity,
					Map.class
			);

			return response.getBody();

		} catch (Exception e) {
			throw new RuntimeException("Error fetching the user info", e);
		}
	}

	public UrlDTO getLoginUrl() {
		return UrlDTO.builder().url(createLoginUrl()).build();
	}

	private String createLoginUrl() {
		return cognitoUri +
				"/login?" +
				"response_type=code" +
				"&client_id=" + clientId +
				"&scope=openid"+
				"&redirect_uri=" + URLEncoder.encode(redirectUri);
	}
}
