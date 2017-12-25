package com.ramailo.exceptionmapper;

import javax.ejb.EJBException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ramailo.pojo.ErrorMessage;

@Provider
public class EjbExceptionMapper implements ExceptionMapper<EJBException> {

	public Response toResponse(EJBException e) {
		Throwable t = e.getCause();
		while ((t != null) && !(t instanceof ConstraintViolationException)) {
			t = t.getCause();
		}
		if (t instanceof ConstraintViolationException) {
			ErrorMessage error = ValidationExceptionMapper.buildValidationError((ConstraintViolationException) t);
			return Response.status(Response.Status.BAD_REQUEST).entity(error).type(MediaType.APPLICATION_JSON).build();
		} else {
			ErrorMessage error = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).type(MediaType.APPLICATION_JSON)
					.build();
		}

	}
}
