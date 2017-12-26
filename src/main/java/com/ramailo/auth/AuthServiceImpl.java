package com.ramailo.auth;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ramailo.annotation.Logged;
import com.ramailo.auth.dto.LoginDTO;
import com.ramailo.auth.dto.LoginResponseDTO;
import com.ramailo.auth.dto.TokenDTO;
import com.ramailo.service.IdentityService;
import com.ramailo.util.HashUtility;

@Logged
public class AuthServiceImpl implements AuthService {

	@Inject
	private IdentityService identityService;

	@Inject
	private TokenService tokenService;

	@Inject
	private HashUtility hashService;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Override
	public LoginResponseDTO authenticate(LoginDTO loginDTO) throws AuthenticationException {

		Identity identity = identityService.fetchByEmail(loginDTO.getEmail());

		if (identity == null) {
			throw new AuthenticationException("Invalid email or password");
		}

		if (!hashService.match(loginDTO.getPassword(), identity.getPassword())) {
			throw new AuthenticationException("Invalid email or password");
		}

		TokenDTO tokenDTO = tokenService.issueToken(identity.getId().toString());
		identityService.saveRefreshToken(identity, tokenDTO);

		return buildLoginResponseDTO(identity, tokenDTO);
	}

	private LoginResponseDTO buildLoginResponseDTO(Identity identity, TokenDTO tokenDTO) {
		LoginResponseDTO response = new LoginResponseDTO();
		response.setId(identity.getId());
		response.setEmail(identity.getEmail());
		response.setToken(tokenDTO);

		//response.setRole(identity.getRole());

		return response;
	}
}
