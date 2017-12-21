package com.ramailo.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.json.JsonObject;

import com.ramailo.RequestInfo;
import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.meta.Action;

public class GenericMiddlewareImpl {

	@Inject
	private GenericServiceImpl genericService;

	@Inject
	private ActionProcessorImpl actionProcessor;

	public Object processGetAction(RequestInfo request) throws Exception {
		Optional<Action> action = actionProcessor.findAction(request);
		if (action.isPresent()) {
			return genericService.invokeAction(request, action.get(), null);
		}

		if (request.getPathParams().size() == 1) {
			List<?> result = genericService.find(request);

			return result;
		} else if (request.getPathParams().size() == 2) {
			Object result = genericService.findById(request);

			return result;
		}

		throw new ResourceNotFoundException();
	}

	public Object processPostAction(RequestInfo request, JsonObject body) throws Exception {
		Optional<Action> action = actionProcessor.findAction(request);
		if (action.isPresent()) {
			return genericService.invokeAction(request, action.get(), body);
		}

		if (request.getPathParams().size() == 1) {
			Object result = genericService.create(request, body);

			return result;
		}

		throw new ResourceNotFoundException();
	}

	public Object processPutAction(RequestInfo request, JsonObject body) throws Exception {
		Optional<Action> action = actionProcessor.findAction(request);
		if (action.isPresent()) {
			return genericService.invokeAction(request, action.get(), body);
		}

		if (request.getPathParams().size() == 2) {
			Object result = genericService.update(request, body);

			return result;
		}
		
		throw new ResourceNotFoundException();
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
