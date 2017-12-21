package com.ramailo.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramailo.RequestInfo;
import com.ramailo.annotation.RamailoArg;
import com.ramailo.annotation.RamailoResource;
import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.meta.Action;
import com.ramailo.util.AttributeUtility;
import com.ramailo.util.PkUtility;
import com.ramailo.util.QueryParamUtility.QueryParam;
import com.ramailo.util.TypeCaster;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Stateless
public class GenericService {

	@Inject
	private EntityManager em;

	public List<?> find(RequestInfo request) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery cquery = cb.createQuery(request.getEntityClass());
		//
		Root<?> root = cquery.from(request.getEntityClass());
		cquery.select(root);

		List<Predicate> predicates = new ArrayList<>();

		for (QueryParam param : request.getQueryParams()) {
			try {
				Field field = request.getEntityClass().getDeclaredField(param.getKey());

				if (field.isAnnotationPresent(ManyToOne.class)) {
					Class<?> fieldType = field.getType();
					String stringifyField = fieldType.getAnnotation(RamailoResource.class).stringify();

					Join<Object, Object> join = root.join(field.getName(), JoinType.LEFT);
					predicates.add(cb.equal(join.get(stringifyField), param.getValue()));

				} else {
					if (param.getOperator().equals("eq")) {
						predicates.add(cb.equal(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("lt")) {
						predicates.add(cb.lessThan(root.get(field.getName()), param.getValue().toString()));
					} else if (param.getOperator().equals("lte")) {
						predicates.add(cb.lessThanOrEqualTo(root.get(field.getName()), param.getValue().toString()));
					} else if (param.getOperator().equals("gt")) {
						predicates.add(cb.greaterThan(root.get(field.getName()), param.getValue().toString()));
					} else if (param.getOperator().equals("gte")) {
						predicates.add(cb.greaterThanOrEqualTo(root.get(field.getName()), param.getValue().toString()));
					} else if (param.getOperator().equals("like")) {
						predicates.add(cb.like(root.get(field.getName()), param.getValue() + "%"));
					}
				}
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}

		cquery.where(predicates.toArray(new Predicate[0]));

		Field autoPkField = PkUtility.findAutoPkField(request.getEntityClass());
		if (autoPkField != null) {
			cquery.orderBy(cb.desc(root.get(autoPkField.getName())));
		}

		TypedQuery<?> tquery = em.createQuery(cquery);
		return tquery.getResultList();
	}

	public Object findById(RequestInfo request) {
		Object id = PkUtility.castToPkType(request.getEntityClass(), request.getFirstPathParam());
		Object result = em.find(request.getEntityClass(), id);

		return result;
	}

	private BaseActions<?> baseActions(Object entity) {
		for (Class actionClass : entity.getClass().getAnnotation(RamailoResource.class).actions()) {
			try {
				BaseActions<?> action = (BaseActions<?>) actionClass.getConstructor(entity.getClass())
						.newInstance(entity);

				BeanUtils.setProperty(action, "em", em);

				return action;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private void onBeforeSave(Object entity) {
		BaseActions<?> action = baseActions(entity);
		if (action != null)
			action.onBeforeSave();
	}

	private void onSave(Object entity) {
		BaseActions<?> action = baseActions(entity);
		if (action != null)
			action.onSave();
	}

	private void onBeforeDelete(Object entity) {
		BaseActions<?> action = baseActions(entity);
		if (action != null)
			action.onBeforeDelete();
	}

	private void onDelete(Object entity) {
		BaseActions<?> action = baseActions(entity);
		if (action != null)
			action.onDelete();
	}

	public Object create(RequestInfo resource, JsonObject object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object entity = mapper.readValue(object.toString(), resource.getEntityClass());

			onBeforeSave(entity);
			em.persist(entity);
			em.flush();
			em.refresh(entity);
			onSave(entity);

			return entity;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public Object update(RequestInfo resource, JsonObject object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object idFromUrl = PkUtility.castToPkType(resource.getEntityClass(), resource.getFirstPathParam());
			Object source = mapper.readValue(object.toString(), resource.getEntityClass());
			Object existing = em.find(resource.getEntityClass(), idFromUrl);
			if (existing == null)
				throw new ResourceNotFoundException();

			AttributeUtility.copyAttributes(existing, source);

			onBeforeSave(existing);
			em.merge(existing);
			em.flush();
			em.refresh(existing);
			onSave(existing);

			return existing;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void remove(RequestInfo resource) {
		Object id = PkUtility.castToPkType(resource.getEntityClass(), resource.getFirstPathParam());
		Object existing = em.find(resource.getEntityClass(), id);
		if (existing == null)
			throw new ResourceNotFoundException();

		onBeforeDelete(existing);
		em.remove(existing);
		em.flush();
		onDelete(existing);
	}

	private Method findMethodinClass(Class clazz, String methodName) {
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName))
				return method;
		}
		return null;
	}

	private Object invokeMethod(Object actionObject, Method method, Object... args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(actionObject, args);
	}

	private Object invokeStaticAction(RequestInfo request, Action action, JsonObject data)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			SecurityException, JsonParseException, JsonMappingException, ClassCastException, IOException {
		Class actionImplClass = request.getEntityClass().getAnnotation(RamailoResource.class).actions()[0];
		Method method = findMethodinClass(actionImplClass, action.getName());

		Object arguments[] = new Object[0];

		if (action.getMethodType().equals("GET") || action.getMethodType().equals("DELETE"))
			arguments = buildArgumentsForAction(actionImplClass, action, request.getQueryParams());
		else
			arguments = buildArgumentsForAction(actionImplClass, action, data);

		return invokeMethod(null, method, arguments);
	}

	private Object invokeNonStaticAction(RequestInfo request, Action action, JsonObject data)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			SecurityException, JsonParseException, JsonMappingException, ClassCastException, IOException {
		Object entity = this.findById(request);
		if (entity == null)
			throw new ResourceNotFoundException();
		
		BaseActions<?> actionImplObject = baseActions(entity);
		Method method = findMethodinClass(actionImplObject.getClass(), action.getName());

		Object arguments[] = new Object[0];

		if (action.getMethodType().equals("GET") || action.getMethodType().equals("DELETE"))
			arguments = buildArgumentsForAction(actionImplObject.getClass(), action, request.getQueryParams());
		else {
			arguments = buildArgumentsForAction(actionImplObject.getClass(), action, data);
		}
		return invokeMethod(actionImplObject, method, arguments);
	}

	public Object invokeAction(RequestInfo request, Action action, JsonObject body)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			SecurityException, JsonParseException, JsonMappingException, ClassCastException, IOException {
		if (action.isStaticMethod()) {
			return invokeStaticAction(request, action, body);
		} else {
			return invokeNonStaticAction(request, action, body);
		}
	}

	private QueryParam findParam(List<QueryParam> params, String paramName) {
		for (QueryParam qp : params) {
			if (qp.getKey().equals(paramName)) {
				return qp;
			}
		}
		return null;
	}

	private Object newInstanceWithId(Class clazz, String id) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, SecurityException {
		Object obj = clazz.getConstructors()[0].newInstance();
		BeanUtils.setProperty(obj, "id", id);

		return obj;
	}

	private Object[] buildArgumentsForAction(Class<?> actionClass, Action action, List<QueryParam> queryParams)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			SecurityException {
		List<Object> arguments = new ArrayList<>();
		Method method = findMethodinClass(actionClass, action.getName());

		for (Parameter parameter : method.getParameters()) {
			RamailoArg arg = parameter.getAnnotation(RamailoArg.class);
			QueryParam paramValue = findParam(queryParams, arg.name());
			if (paramValue != null) {
				Object castedValue = null;
				try {
					castedValue = TypeCaster.cast(paramValue.getValue().toString(), parameter.getType());
				} catch (ClassCastException e) {
					castedValue = newInstanceWithId(parameter.getType(), paramValue.getValue().toString());
				}
				arguments.add(castedValue);
			}
		}

		return arguments.toArray();
	}

	private Object[] buildArgumentsForAction(Class<?> actionClass, Action action, JsonObject data)
			throws ClassCastException, JsonParseException, JsonMappingException, IOException {
		List<Object> arguments = new ArrayList<>();
		Method method = findMethodinClass(actionClass, action.getName());

		for (Parameter parameter : method.getParameters()) {
			RamailoArg arg = parameter.getAnnotation(RamailoArg.class);
			JsonValue value = data.get(arg.name());
			if (value != null) {
				Object castedValue = null;
				if (value.getValueType().equals(ValueType.OBJECT)) {
					castedValue = new ObjectMapper().readValue(value.toString(), parameter.getType());
				} else {
					castedValue = TypeCaster.cast(value.toString(), parameter.getType());
				}
				arguments.add(castedValue);
			}
		}

		return arguments.toArray();
	}
}
