package com.ramailo.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ramailo.pojo.ErrorMessage;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

	public Response toResponse(Exception exception) {
		exception.printStackTrace();
		ErrorMessage error = new ErrorMessage(exception.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).type(MediaType.APPLICATION_JSON)
				.build();
	}
}
