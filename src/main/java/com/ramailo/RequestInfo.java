package com.ramailo;

import java.util.ArrayList;
import java.util.List;

import com.ramailo.meta.Resource;
import com.ramailo.util.QueryParamUtility.QueryParam;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class RequestInfo {
	private Resource resource;
	private Class<?> entityClass;
	private String methodType;
	private List<String> pathParams = new ArrayList<>();
	private List<QueryParam> queryParams = new ArrayList<>();

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public List<QueryParam> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(List<QueryParam> queryParams) {
		this.queryParams = queryParams;
	}

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

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
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
