package com.ramailo.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.json.JsonObject;

import com.ramailo.RequestInfo;
import com.ramailo.meta.Action;

public class GenericMiddleware {

	@Inject
	private GenericService genericService;
	
	@Inject
	private ActionProcessor actionProcessor;

	public Object processGetAction(RequestInfo request) throws Exception {

		Optional<Action> action = actionProcessor.findAction(request);
		if (action.isPresent()) {
			return genericService.invokeAction(request, action.get());
		}

		if (request.getFirstPathParam() != null) {
			Object result = genericService.findById(request);
			
			return result;
		}

		List<?> result = genericService.find(request);

		return result;
	}

	public Object processPostAction(RequestInfo requestInfo, JsonObject body) throws Exception {
		Object result = genericService.create(requestInfo, body);

		return result;
	}

	public Object processPutAction(RequestInfo requestInfo, JsonObject body) throws Exception {
		Object result = genericService.update(requestInfo, body);

		return result;
	}

	public void processDeleteAction(RequestInfo requestInfo) throws Exception {
		genericService.remove(requestInfo);
	}
}
