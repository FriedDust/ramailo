package com.ramailo.exceptionmapper;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ramailo.pojo.ErrorMessage;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

	@Override
	public Response toResponse(BadRequestException ex) {
		ErrorMessage error = new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(), ex.getMessage());
		return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
	}
}