package com.ramailo.dto.auth;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangePasswordDTO {

	@NotNull
	@Size(min = 6, max = 50)
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
