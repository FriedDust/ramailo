package com.ramailo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class ResourceMeta {
	private String resource;
	private Class<?> entityClass;
	private List<String> pathParams = new ArrayList<>();

	public List<String> getPathParams() {
		return pathParams;
	}

	public void setPathParams(List<String> pathParams) {
		this.pathParams = pathParams;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getFirstPathParam() {
		return pathParams.size() > 1 ? pathParams.get(1) : null;
	}

	public String getSecondPathParam() {
		return pathParams.size() > 2 ? pathParams.get(2) : null;
	}

	public String getThirdPathParam() {
		return pathParams.size() > 3 ? pathParams.get(3) : null;
	}
}
