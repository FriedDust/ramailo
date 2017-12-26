package com.ramailo.auth;

import com.ramailo.dto.auth.LoginDTO;

public interface AuthService {

	public Identity authenticate(LoginDTO loginDTO) throws AuthenticationException;
}
