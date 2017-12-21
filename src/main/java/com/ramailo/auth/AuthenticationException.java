package com.ramailo.auth;

public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = -4929311263865246047L;

	public AuthenticationException() {
		super("Authentication Exception");
	}

	public AuthenticationException(String message) {
		super(message);
	}
}
