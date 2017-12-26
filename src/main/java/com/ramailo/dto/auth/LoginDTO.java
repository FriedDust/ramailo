package com.ramailo.dto.auth;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class LoginDTO {

	@NotNull(message = "email cannot be null")
	@Pattern(regexp = ".+@.+", message = "invalid email format")
	private String email;

	@NotNull(message = "password cannot be null")
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
