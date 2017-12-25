package com.ramailo.auth;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ramailo.pojo.ErrorMessage;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

	public Response toResponse(AuthenticationException exception) {
		ErrorMessage error = new ErrorMessage(Response.Status.UNAUTHORIZED.getStatusCode(), exception.getMessage());
		return Response.status(Response.Status.UNAUTHORIZED).entity(error).type(MediaType.APPLICATION_JSON).build();
	}
}
