package com.ramailo.exceptionmapper;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ramailo.pojo.ErrorMessage;
import com.ramailo.pojo.FieldSpecificErrorMessage;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		ErrorMessage error = buildValidationError(exception);
		return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
	}

	public static ErrorMessage buildValidationError(ConstraintViolationException exception) {
		ErrorMessage error = new ErrorMessage();
		error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
		error.setMessage("Validation exception");

		for (ConstraintViolation<?> e : exception.getConstraintViolations()) {
			FieldSpecificErrorMessage errorMessage = new FieldSpecificErrorMessage();
			errorMessage.setMessage(e.getMessage());
			errorMessage.setField(getFieldName(e.getPropertyPath()));
			error.getErrors().add(errorMessage);
		}

		return error;
	}

	private static String getFieldName(Path propertyPath) {
		String[] pathValues = propertyPath.toString().split("\\.");
		return pathValues[pathValues.length - 1];
	}
}