package com.ramailo.auth;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ramailo.annotation.Logged;
import com.ramailo.dto.auth.LoginDTO;
import com.ramailo.service.IdentityService;
import com.ramailo.util.HashUtility;

@Logged
public class AuthServiceImpl implements AuthService {

	@Inject
	private IdentityService identityService;

	@Inject
	private HashUtility hashService;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

	@Override
	public Identity authenticate(LoginDTO loginDTO) throws AuthenticationException {

		Identity user = identityService.fetchByEmail(loginDTO.getEmail());

		if (user == null) {
			throw new AuthenticationException("Invalid email or password");
		}

		if (hashService.match(loginDTO.getPassword(), user.getPassword())) {
			return user;
		}

		throw new AuthenticationException("Invalid email or password");
	}
}
