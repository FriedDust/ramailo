package com.ramailo.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class ErrorMessage {
	private Integer status;
	private String message;

	private List<FieldSpecificErrorMessage> errors = new ArrayList<>();

	public ErrorMessage() {
	}

	public ErrorMessage(Integer status, String message) {
		this.status = status;
		this.message = message;
	}

	public List<FieldSpecificErrorMessage> getErrors() {
		return errors;
	}

	public void setErrors(List<FieldSpecificErrorMessage> errors) {
		this.errors = errors;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
