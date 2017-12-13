package com.ramailo.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramailo.ResourceMeta;
import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.util.AttributeUtility;
import com.ramailo.util.PkUtility;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Stateless
public class GenericService {

	@Inject
	private EntityManager em;

	public List<?> findAll(ResourceMeta resource) {
		String jpql = "select x from " + resource.getEntityClass().getSimpleName() + " x";
		List<?> result = em.createQuery(jpql).getResultList();

		return result;
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
