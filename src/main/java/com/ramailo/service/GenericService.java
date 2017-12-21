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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramailo.ResourceMeta;
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

	public List<?> find(ResourceMeta resource, List<QueryParam> params) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery cquery = cb.createQuery(resource.getEntityClass());
		//
		Root<?> root = cquery.from(resource.getEntityClass());
		cquery.select(root);

		List<Predicate> predicates = new ArrayList<>();

		for (QueryParam param : params) {
			try {
				Field field = resource.getEntityClass().getDeclaredField(param.getKey());

				if (field.isAnnotationPresent(ManyToOne.class)) {
					Class<?> fieldType = field.getType();
					String stringifyField = fieldType.getAnnotation(RamailoResource.class).stringify();

					Join<Object, Object> join = root.join(field.getName(), JoinType.LEFT);
					predicates.add(cb.equal(join.get(stringifyField), param.getValue()));

				} else {
					if (param.getOperator().equals("eq")) {
						predicates.add(cb.equal(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("lt")) {
						predicates.add(cb.lessThan(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("lte")) {
						predicates.add(cb.lessThanOrEqualTo(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("gt")) {
						predicates.add(cb.greaterThan(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("gte")) {
						predicates.add(cb.greaterThanOrEqualTo(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("like")) {
						predicates.add(cb.like(root.get(field.getName()), param.getValue() + "%"));
					}
				}
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}

		cquery.where(predicates.toArray(new Predicate[0]));

		Field autoPkField = PkUtility.findAutoPkField(resource.getEntityClass());
		if (autoPkField != null) {
			cquery.orderBy(cb.desc(root.get(autoPkField.getName())));
		}

		TypedQuery<?> tquery = em.createQuery(cquery);
		return tquery.getResultList();
	}

	public Object findById(ResourceMeta resource) {
		Object id = PkUtility.castToPkType(resource.getEntityClass(), resource.getFirstPathParam());
		Object result = em.find(resource.getEntityClass(), id);

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

	public Object create(ResourceMeta resource, JsonObject object) {
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

	public Object update(ResourceMeta resource, JsonObject object) {
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

	public void remove(ResourceMeta resource) {
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

	public Object invokeStaticAction(ResourceMeta resourceMeta, Action action, List<QueryParam> queryParams)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			SecurityException {
		Class actionClass = resourceMeta.getEntityClass().getAnnotation(RamailoResource.class).actions()[0];
		Method method = findMethodinClass(actionClass, action.getName());

		Object arguments[] = buildArgumentsForAction(actionClass, action, queryParams);
		return invokeMethod(null, method, arguments);
	}

	public Object invokeAction(ResourceMeta resourceMeta, Action action, List<QueryParam> queryParams)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			SecurityException {
		Object entity = this.findById(resourceMeta);
		BaseActions<?> actionObject = baseActions(entity);
		Method method = findMethodinClass(actionObject.getClass(), action.getName());

		Object arguments[] = buildArgumentsForAction(actionObject.getClass(), action, queryParams);
		return invokeMethod(actionObject, method, arguments);
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
			if (arg.name().equals(paramValue.getKey())) {
				Object castedValue = null;
				try {
					castedValue = TypeCaster.cast(paramValue.getValue(), parameter.getType());
				} catch (ClassCastException e) {
					castedValue = newInstanceWithId(parameter.getType(), paramValue.getValue());
				}
				arguments.add(castedValue);
			}
		}

		return arguments.toArray();
	}
}
