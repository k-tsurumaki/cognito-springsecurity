package com.example.cognito_springsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final RestTemplate restTemplate;

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
	private String jwkSetUri;

	@Autowired
	public SecurityConfig(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(
				"http://localhost:8080"
		));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(c -> c.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable) // APIならいらない
				.exceptionHandling(customizer -> customizer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))) // 未認証ユーザーのリクエストに対して401 UNAUTHORIZEDステータスを返す
				.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // セッションはステートレスに設定
				.authorizeHttpRequests((authorize) -> authorize
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

		return http.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
				.restOperations(restTemplate)  // カスタムRestTemplateを設定
				.build();
	}
}
