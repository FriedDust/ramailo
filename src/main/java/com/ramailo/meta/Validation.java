package com.ramailo.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Validation {
	
	@JsonIgnore
	public String getValidationName();
}
