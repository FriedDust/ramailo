package com.ramailo.auth;

import com.ramailo.auth.dto.LoginDTO;
import com.ramailo.auth.dto.LoginResponseDTO;

public interface AuthService {

	public LoginResponseDTO authenticate(LoginDTO loginDTO) throws AuthenticationException;
}
