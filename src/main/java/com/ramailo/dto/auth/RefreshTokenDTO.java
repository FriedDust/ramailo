package com.ramailo.dto.auth;

import javax.validation.constraints.NotNull;

public class RefreshTokenDTO {

	@NotNull(message = "refresh token cannot be null or empty")
	private String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}