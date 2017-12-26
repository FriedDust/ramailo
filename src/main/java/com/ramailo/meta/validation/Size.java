package com.ramailo.meta.validation;

public class Size implements Validation {

	private Integer min;
	private Integer max = Integer.MAX_VALUE;

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}
	
	@Override
	public String getValidationName() {
		return "size";
	}
}
