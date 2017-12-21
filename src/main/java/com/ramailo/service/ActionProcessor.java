package com.ramailo.service;

import java.util.Optional;

import com.ramailo.RequestInfo;
import com.ramailo.meta.Action;

public class ActionProcessor {

	public Optional<Action> findStaticAction(RequestInfo request) {

		if (request.getFirstPathParam() != null) {
			Optional<Action> staticAction = request.getResource().getStaticActions().stream()
					.filter(act -> act.isStaticMethod() && act.getMethodType().equals(request.getMethodType())
							&& act.getPathName().equals(request.getFirstPathParam()))
					.findFirst();
			return staticAction;
		}

		return Optional.empty();
	}

	public Optional<Action> findNonStaticAction(RequestInfo request) {

		if (request.getSecondPathParam() != null) {
			Optional<Action> action = request.getResource().getActions().stream()
					.filter(act -> !act.isStaticMethod() && act.getMethodType().equals(request.getMethodType())
							&& act.getPathName().equals(request.getSecondPathParam()))
					.findFirst();

			return action;
		}

		return Optional.empty();
	}

	public Optional<Action> findAction(RequestInfo request) {
		Optional<Action> action = findNonStaticAction(request);
		if (action.isPresent())
			return action;

		action = findStaticAction(request);
		if (action.isPresent())
			return action;

		return Optional.empty();
	}
}
