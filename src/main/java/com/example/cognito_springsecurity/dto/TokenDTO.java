package com.example.cognito_springsecurity.dto;

public class TokenDTO {
	private String idToken;
	private String accessToken;
	private Long userId;

	// 全てのフィールドを含むコンストラクタ
	public TokenDTO(String idToken, String accessToken, Long userId) {
		this.idToken = idToken;
		this.accessToken = accessToken;
		this.userId = userId;
	}

	public String getIdToken() {
		return idToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public Long getUserId() {
		return userId;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public static class Builder {
		private String idToken;
		private String accessToken;
		private Long userId;

		public Builder idToken(String idToken) {
			this.idToken = idToken;
			return this;
		}

		public Builder accessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public TokenDTO build() {
			return new TokenDTO(idToken, accessToken, userId);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "TokenDTO{" +
				"idToken='" + idToken + '\'' +
				", accessToken='" + accessToken + '\'' +
				", userId=" + userId +
				'}';
	}
}

