package com.ramailo.meta;

public class Mandatory implements Validation {

	private Boolean mandatory = false;

	public Boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}
}
