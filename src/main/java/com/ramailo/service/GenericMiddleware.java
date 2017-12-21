package com.ramailo.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.json.JsonObject;

import com.ramailo.RequestInfo;
import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.meta.Action;

public class GenericMiddleware {

	@Inject
	private GenericService genericService;

	@Inject
	private ActionProcessor actionProcessor;

	public Object processGetAction(RequestInfo request) throws Exception {
		Optional<Action> action = actionProcessor.findAction(request);
		if (action.isPresent()) {
			return genericService.invokeAction(request, action.get(), null);
		}

		if (request.getFirstPathParam() != null && request.getPathParams().size() == 2) {
			Object result = genericService.findById(request);

			return result;
		}

		List<?> result = genericService.find(request);

		return result;
	}

	public Object processPostAction(RequestInfo request, JsonObject body) throws Exception {
		Optional<Action> action = actionProcessor.findAction(request);
		if (action.isPresent()) {
			return genericService.invokeAction(request, action.get(), body);
		}

		Object result = genericService.create(request, body);

		return result;
	}

	public Object processPutAction(RequestInfo request, JsonObject body) throws Exception {
		Optional<Action> action = actionProcessor.findAction(request);
		if (action.isPresent()) {
			return genericService.invokeAction(request, action.get(), body);
		}

		Object result = genericService.update(request, body);

		return result;
	}

	public void processDeleteAction(RequestInfo request) throws Exception {
		Optional<Action> action = actionProcessor.findAction(request);
		if (action.isPresent()) {
			genericService.invokeAction(request, action.get(), null);
			return;
		}
		if (request.getPathParams().size() == 2) {
			genericService.remove(request);
			return;
		}

		throw new ResourceNotFoundException();
	}
}
