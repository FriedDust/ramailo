package com.ramailo.auth.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class ForgotPasswordDTO {

	@NotNull
	@Pattern(regexp = ".+@.+", message = "invalid email format")
	//@JsonDeserialize(using = LowerCaseDeserializer.class)
	private String email;

	@NotNull
	private String redirectUrl;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
