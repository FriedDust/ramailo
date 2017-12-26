package com.ramailo.auth;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ramailo.auth.dto.LoginDTO;
import com.ramailo.auth.dto.RefreshTokenDTO;

@Path("/auth")
public class AuthRs {

	@Inject
	private AuthService authService;

	@Inject
	private TokenService tokenService;

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Object login(@NotNull @Valid LoginDTO loginDTO) {
		return Response.ok(authService.authenticate(loginDTO)).build();
	}

	@POST
	@Path("/refresh-token")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Object login(@NotNull @Valid RefreshTokenDTO refreshTokenDTO) {
		return Response.ok(tokenService.getNewAccessToken(refreshTokenDTO)).build();
	}
}
