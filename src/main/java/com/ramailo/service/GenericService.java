package com.ramailo.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramailo.ResourceMeta;
import com.ramailo.exception.ResourceNotFoundException;
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
		Object id = PkUtility.castToPkType(resource);
		Object result = em.find(resource.getEntityClass(), id);

		return result;
	}

	public Object create(ResourceMeta resource, JsonObject object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object entity = mapper.readValue(object.toString(), resource.getEntityClass());
			em.persist(entity);
			em.flush();

			return entity;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public Object update(ResourceMeta resource, JsonObject object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object id = PkUtility.castToPkType(resource);
			Object entity = mapper.readValue(object.toString(), resource.getEntityClass());
			Object existing = em.find(resource.getEntityClass(), id);
			if (existing == null)
				throw new ResourceNotFoundException();

			BeanUtils.setProperty(entity, "id", id);

			em.merge(entity);
			em.flush();

			return entity;
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void remove(ResourceMeta resource) {
		Object id = PkUtility.castToPkType(resource);
		Object existing = em.find(resource.getEntityClass(), id);
		if (existing == null)
			throw new ResourceNotFoundException();

		em.remove(existing);
		em.flush();
	}
}
