package com.ramailo.auth;

import java.util.Map;

import com.ramailo.auth.dto.RefreshTokenDTO;
import com.ramailo.auth.dto.TokenDTO;

public interface TokenService {
	String generateToken(String userJson, Long timeInMinutes, String tokenType);

	Map<String, Object> validateToken(String token);

	Identity getUserInfo(String token);

	String getTokenType(String token);

	TokenDTO getNewAccessToken(RefreshTokenDTO refreshTokenDTO);

	TokenDTO issueToken(String userInfo);
}
