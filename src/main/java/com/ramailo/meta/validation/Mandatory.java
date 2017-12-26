package com.ramailo.meta;

public class Mandatory implements Validation {

	private Boolean value = false;
	
	public Mandatory(Boolean value) {
		this.value = value;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

	@Override
	public String getValidationName() {
		return "mandatory";
	}
}
