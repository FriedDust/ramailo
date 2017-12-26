package com.ramailo.exceptionmapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ramailo.auth.TokenException;
import com.ramailo.pojo.ErrorMessage;

@Provider
public class TokenExceptionMapper implements ExceptionMapper<TokenException> {

	@Override
	public Response toResponse(TokenException ex) {
		ErrorMessage error = new ErrorMessage(Response.Status.UNAUTHORIZED.getStatusCode(), ex.getMessage());
		return Response.status(error.getStatus()).entity(error).build();
	}
}
