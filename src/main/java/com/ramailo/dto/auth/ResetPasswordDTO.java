package com.ramailo.dto.auth;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ResetPasswordDTO {

	@NotNull
	@Pattern(regexp = ".+@.+", message = "invalid email format")
	private String email;

	@NotNull
	@Size(min = 6, max = 50)
	private String password;

	@NotNull
	private String code;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
