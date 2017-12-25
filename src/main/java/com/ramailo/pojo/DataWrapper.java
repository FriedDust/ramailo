package com.ramailo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DataWrapper {

	private Long count;
	private Object data;

	public DataWrapper() {

	}

	public DataWrapper(Object data) {
		this.data = data;
	}

	public DataWrapper(Object data, Long count) {
		this.data = data;
		this.count = count;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}