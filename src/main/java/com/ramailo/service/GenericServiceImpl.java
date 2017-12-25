package com.ramailo.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;

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
public class GenericServiceImpl {

	static int PAGE_SIZE = 5;

	@Inject
	private EntityManager em;

	private Predicate buildPredicate(Field field, CriteriaBuilder cb, Root<?> root, QueryParam param) {
		String fieldName = field.getName();
		String value = param.getValue().toString();
		String operator = param.getOperator();

		if (field.isAnnotationPresent(ManyToOne.class)) {
			Class<?> fieldType = field.getType();
			String stringifyField = fieldType.getAnnotation(RamailoResource.class).stringify();

			Join<Object, Object> join = root.join(field.getName(), JoinType.LEFT);
			return cb.equal(join.get(stringifyField), value);

		} else {
			Predicate predicate = null;
			if (operator.equals("eq")) {
				predicate = cb.equal(root.get(fieldName), value);
			} else if (operator.equals("lt")) {
				predicate = cb.lessThan(root.get(fieldName), value);
			} else if (operator.equals("lte")) {
				predicate = cb.lessThanOrEqualTo(root.get(fieldName), value);
			} else if (operator.equals("gt")) {
				predicate = cb.greaterThan(root.get(fieldName), value);
			} else if (operator.equals("gte")) {
				predicate = cb.greaterThanOrEqualTo(root.get(fieldName), value);
			} else if (operator.equals("like")) {
				predicate = cb.like(root.get(fieldName), value + "%");
			}

			return predicate;
		}
	}

	public List<?> find(RequestInfo request) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery cquery = cb.createQuery(request.getEntityClass());

		Root<?> root = cquery.from(request.getEntityClass());
		cquery.select(root);

		List<Predicate> predicates = new ArrayList<>();

		for (QueryParam param : request.getQueryParams()) {
			try {
				Field field = request.getEntityClass().getDeclaredField(param.getKey());
				Predicate predicate = buildPredicate(field, cb, root, param);

				if (predicate != null)
					predicates.add(predicate);
			} catch (NoSuchFieldException | SecurityException e) {
				continue;
			}
		}

		cquery.where(predicates.toArray(new Predicate[0]));

		cquery.orderBy(order(request.getQueryParams(), cb, cquery, root).toArray(new Order[0]));

		TypedQuery<?> tquery = em.createQuery(cquery);
		paginate(request.getQueryParams(), tquery);
		return tquery.getResultList();
	}

	private List<Order> order(List<QueryParam> params, CriteriaBuilder cb, CriteriaQuery cquery, Root root) {
		List<Order> orders = new ArrayList<>();
		for (QueryParam param : params) {
			if (!param.getKey().equals("orderBy"))
				continue;
			if (param.getOperator().equals("desc")) {
				orders.add(cb.desc(root.get(param.getValue().toString())));
			} else {
				orders.add(cb.asc(root.get(param.getValue().toString())));
			}
		}

		return orders;
	}

	private void paginate(List<QueryParam> params, TypedQuery<?> tquery) {
		Optional<QueryParam> startParam = params.stream().filter(qp -> qp.getKey().equals("start")).findFirst();
		Optional<QueryParam> offsetParam = params.stream().filter(qp -> qp.getKey().equals("offset")).findFirst();

		int start = 1;
		int offset = PAGE_SIZE;

		try {
			start = startParam.isPresent() ? Integer.valueOf(startParam.get().getValue().toString()) : start;
			start--;
		} catch (NumberFormatException nfe) {
		}

		try {
			offset = offsetParam.isPresent() ? Integer.valueOf(offsetParam.get().getValue().toString()) : offset;
		} catch (NumberFormatException nfe) {
		}

		tquery.setFirstResult(start);
		tquery.setMaxResults(offset);
	}

	public Object findById(RequestInfo request) {
		Object id = PkUtility.castToPkType(request.getEntityClass(), request.getFirstPathParam());
		Object result = em.find(request.getEntityClass(), id);

		if (result == null)
			throw new ResourceNotFoundException();

		return result;
	}

	private Optional<BaseAction<?>> baseAction(Object entity) {
		try {
			Class<?> actionClass = entity.getClass().getAnnotation(RamailoResource.class).actions()[0];
			BaseAction<?> action = (BaseAction<?>) actionClass.getConstructor(entity.getClass()).newInstance(entity);

			BeanUtils.setProperty(action, "em", em);

			return Optional.of(action);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			return Optional.empty();
		} catch (ArrayIndexOutOfBoundsException e) {
			return Optional.empty();
		}
	}

	private void onBeforeSave(Object entity) {
		Optional<BaseAction<?>> action = baseAction(entity);
		if (action.isPresent())
			action.get().onBeforeSave();
	}

	private void onSave(Object entity) {
		Optional<BaseAction<?>> action = baseAction(entity);
		if (action.isPresent())
			action.get().onSave();
	}

	private void onBeforeDelete(Object entity) {
		Optional<BaseAction<?>> action = baseAction(entity);
		if (action.isPresent())
			action.get().onBeforeDelete();
	}

	private void onDelete(Object entity) {
		Optional<BaseAction<?>> action = baseAction(entity);
		if (action.isPresent())
			action.get().onDelete();
	}

	public Object create(RequestInfo resource, JsonObject object) {
		Object entity;
		try {
			ObjectMapper mapper = new ObjectMapper();
			entity = mapper.readValue(object.toString(), resource.getEntityClass());

		} catch (IOException e) {
			throw new RuntimeException("Cannot convert body to " + resource.getEntityClass());
		}

		onBeforeSave(entity);
		em.persist(entity);
		em.flush();
		em.refresh(entity);
		onSave(entity);

		return entity;

	}

	public Object update(RequestInfo resource, JsonObject object) {
		Object source;
		try {
			ObjectMapper mapper = new ObjectMapper();
			source = mapper.readValue(object.toString(), resource.getEntityClass());
		} catch (IOException e) {
			throw new RuntimeException("Cannot convert body to " + resource.getEntityClass());
		}

		Object idFromUrl = PkUtility.castToPkType(resource.getEntityClass(), resource.getFirstPathParam());

		Object existing = em.find(resource.getEntityClass(), idFromUrl);
		if (existing == null)
			throw new ResourceNotFoundException();

		try {
			AttributeUtility.copyAttributes(existing, source);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException("Cannot merge attributes to the existing object.");
		}

		onBeforeSave(existing);
		em.merge(existing);
		em.flush();
		em.refresh(existing);
		onSave(existing);

		return existing;

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

	private Object invokeMethod(Object actionObject, Method method, Object... args) {
		try {
			return method.invoke(actionObject, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Cannot invoke method: " + method.getName());
		}
	}

	private Object invokeStaticAction(RequestInfo request, Action action, JsonObject data) {
		Class<?> actionImplClass = null;
		try {
			actionImplClass = request.getEntityClass().getAnnotation(RamailoResource.class).actions()[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException("Actions class not present.");
		}
		Optional<Method> method = Arrays.stream(actionImplClass.getMethods())
				.filter(m -> m.getName().equals(action.getName())).findFirst();

		Object arguments[] = new Object[0];

		if (action.getMethodType().equals("GET") || action.getMethodType().equals("DELETE"))
			arguments = buildArgumentsForAction(actionImplClass, action, request.getQueryParams());
		else
			arguments = buildArgumentsForAction(actionImplClass, action, data);

		return invokeMethod(null, method.get(), arguments);
	}

	private Object invokeNonStaticAction(RequestInfo request, Action action, JsonObject data) {
		Object entity = this.findById(request);
		if (entity == null)
			throw new ResourceNotFoundException();

		Optional<BaseAction<?>> actionImplObject = baseAction(entity);
		Optional<Method> method = Arrays.stream(actionImplObject.get().getClass().getMethods())
				.filter(m -> m.getName().equals(action.getName())).findFirst();

		Object arguments[] = new Object[0];

		if (action.getMethodType().equals("GET") || action.getMethodType().equals("DELETE"))
			arguments = buildArgumentsForAction(actionImplObject.getClass(), action, request.getQueryParams());
		else {
			arguments = buildArgumentsForAction(actionImplObject.getClass(), action, data);
		}
		return invokeMethod(actionImplObject, method.get(), arguments);
	}

	public Object invokeAction(RequestInfo request, Action action, JsonObject body) {
		if (action.isStaticMethod()) {
			return invokeStaticAction(request, action, body);
		} else {
			return invokeNonStaticAction(request, action, body);
		}
	}

	private Object newInstanceWithId(Class<?> clazz, String id) {
		try {
			Object obj = clazz.getConstructors()[0].newInstance();
			BeanUtils.setProperty(obj, "id", id);

			return obj;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			throw new RuntimeException("Cannot create instance of " + clazz.getName() + " and set ID");
		}

	}

	private Object[] buildArgumentsForAction(Class<?> actionClass, Action action, List<QueryParam> queryParams) {
		List<Object> arguments = new ArrayList<>();
		Optional<Method> method = Arrays.stream(actionClass.getMethods())
				.filter(m -> m.getName().equals(action.getName())).findFirst();

		for (Parameter parameter : method.get().getParameters()) {
			RamailoArg arg = parameter.getAnnotation(RamailoArg.class);
			Optional<QueryParam> paramValue = queryParams.stream().filter(qp -> qp.getKey().equals(arg.name()))
					.findFirst();
			if (paramValue.isPresent()) {
				Object castedValue = null;
				try {
					castedValue = TypeCaster.cast(paramValue.get().getValue().toString(), parameter.getType());
				} catch (ClassCastException e) {
					castedValue = newInstanceWithId(parameter.getType(), paramValue.get().getValue().toString());
				}
				arguments.add(castedValue);
			}
		}

		return arguments.toArray();
	}

	private Object[] buildArgumentsForAction(Class<?> actionClass, Action action, JsonObject data) {
		List<Object> arguments = new ArrayList<>();
		Optional<Method> method = Arrays.stream(actionClass.getMethods())
				.filter(m -> m.getName().equals(action.getName())).findFirst();

		for (Parameter parameter : method.get().getParameters()) {
			RamailoArg arg = parameter.getAnnotation(RamailoArg.class);
			JsonValue value = data.get(arg.name());
			if (value != null) {
				Object castedValue = null;
				if (value.getValueType().equals(ValueType.OBJECT)) {
					try {
						castedValue = new ObjectMapper().readValue(value.toString(), parameter.getType());
					} catch (IOException e) {
						throw new RuntimeException("Cannot convert value to " + parameter.getType());
					}
				} else {
					castedValue = TypeCaster.cast(value.toString(), parameter.getType());
				}
				arguments.add(castedValue);
			}
		}

		return arguments.toArray();
	}
}
