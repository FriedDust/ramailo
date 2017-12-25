package com.ramailo.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.pojo.ErrorMessage;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

	@Override
	public Response toResponse(ResourceNotFoundException exception) {
		ErrorMessage error = new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(), exception.getMessage());
		return Response.status(Response.Status.NOT_FOUND).entity(error).type(MediaType.APPLICATION_JSON).build();
	}
}
