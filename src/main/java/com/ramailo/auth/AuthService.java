package com.ramailo.auth;

public interface AuthService {

	public Identity authenticate(String email, String password) throws AuthenticationException;
}
