package com.example.cognito_springsecurity.dto;

public class UrlDTO {
	private String url;

	public UrlDTO(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static class Builder {
		private String url;

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public UrlDTO build() {
			return new UrlDTO(url);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "UrlDTO{" +
				"url='" + url + '\'' +
				'}';
	}
}

