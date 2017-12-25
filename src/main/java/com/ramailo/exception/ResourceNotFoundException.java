package com.ramailo.exception;

import javax.ejb.ApplicationException;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@ApplicationException(rollback = true)
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1386105659432962520L;

	public ResourceNotFoundException() {
		super("Resource Not Found");
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
