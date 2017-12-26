package com.ramailo.meta.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Validation {
	
	@JsonIgnore
	public String getValidationName();
}
