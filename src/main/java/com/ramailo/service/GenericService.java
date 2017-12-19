package com.ramailo.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramailo.ResourceMeta;
import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.util.AttributeUtility;
import com.ramailo.util.PkUtility;
import com.ramailo.util.QueryParamUtility;
import com.ramailo.util.QueryParamUtility.QueryParam;

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
		//cquery.from(resource.getEntityClass());
		cquery.select(root);

		for (QueryParam param : params) {
			try {
				Field field = resource.getEntityClass().getDeclaredField(param.getKey());
				
				if (field.isAnnotationPresent(ManyToOne.class)) {
					
				} else {
					if (param.getOperator().equals("eq")) {
						cquery.where(cb.equal(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("lt")) {
						cquery.where(cb.lessThan(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("lte")) {
						cquery.where(cb.lessThanOrEqualTo(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("gt")) {
						cquery.where(cb.greaterThan(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("gte")) {
						cquery.where(cb.greaterThanOrEqualTo(root.get(field.getName()), param.getValue()));
					} else if (param.getOperator().equals("like")) {
						cquery.where(cb.like(root.get(field.getName()), param.getValue() + "%"));
					}
				}
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}

		Field autoPkField = PkUtility.findAutoPkField(resource.getEntityClass());
		if (autoPkField != null) {
			cquery.orderBy(cb.desc(root.get(autoPkField.getName())));
		}

		TypedQuery<?> tquery = em.createQuery(cquery);
		return tquery.getResultList();
	}

	public Object findById(ResourceMeta resource) {
		Object id = PkUtility.castToPkType(resource.getEntityClass(), resource.getResourceId());
		Object result = em.find(resource.getEntityClass(), id);

		return result;
	}

	public Object create(ResourceMeta resource, JsonObject object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object entity = mapper.readValue(object.toString(), resource.getEntityClass());
			em.persist(entity);
			em.flush();
			em.refresh(entity);

			return entity;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public Object update(ResourceMeta resource, JsonObject object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object idFromUrl = PkUtility.castToPkType(resource.getEntityClass(), resource.getResourceId());
			Object source = mapper.readValue(object.toString(), resource.getEntityClass());
			Object existing = em.find(resource.getEntityClass(), idFromUrl);
			if (existing == null)
				throw new ResourceNotFoundException();

			// BeanUtils.copyProperties(existing, entity);
			// BeanUtils.setProperty(entity, "id", id);
			AttributeUtility.copyAttributes(existing, source);

			em.merge(existing);
			em.flush();
			em.refresh(existing);

			return existing;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void remove(ResourceMeta resource) {
		Object id = PkUtility.castToPkType(resource.getEntityClass(), resource.getResourceId());
		Object existing = em.find(resource.getEntityClass(), id);
		if (existing == null)
			throw new ResourceNotFoundException();

		em.remove(existing);
		em.flush();
	}
}
