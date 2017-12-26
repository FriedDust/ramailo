package com.ramailo.meta;

import java.util.HashMap;
import java.util.Map;

import com.ramailo.meta.validation.Validation;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class Attribute {

	private String name;
	private String label;
	private String type;

	private Map<String, Validation> validations = new HashMap<>();

	public Map<String, Validation> getValidations() {
		return validations;
	}

	public void setValidations(Map<String, Validation> validations) {
		this.validations = validations;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
