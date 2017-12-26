package com.ramailo.auth;

public class TokenException extends RuntimeException {
	
	public TokenException() {
		super();
	}

	public TokenException(String message) {
		super(message);
	}
}