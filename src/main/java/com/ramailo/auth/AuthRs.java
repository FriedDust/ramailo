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

import com.ramailo.dto.auth.LoginDTO;

@Path("/auth")
public class AuthRs {

	@Inject
	private AuthService authService;

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Object login(@NotNull @Valid LoginDTO loginDTO) {
		return Response.ok(authService.authenticate(loginDTO)).build();
	}
}
