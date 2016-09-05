package com.ramailo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramailo.exception.ResourceNotFoundException;

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
		Integer id = Integer.valueOf(resource.getId());
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
			throw new RuntimeException();
		}
	}

	public Object update(ResourceMeta resource, Object pk, JsonObject object) {
		ObjectMapper mapper = new ObjectMapper();
		Integer id = Integer.valueOf(pk.toString());
		try {
			Object entity = mapper.readValue(object.toString(), resource.getEntityClass());
			Object existing = em.find(resource.getEntityClass(), id);
			if (existing == null)
				throw new ResourceNotFoundException();

			BeanUtils.setProperty(entity, "id", id);

			em.merge(entity);
			em.flush();

			return entity;
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public void remove(ResourceMeta resource, Object pk) {
		Integer id = Integer.valueOf(pk.toString());
		Object existing = em.find(resource.getEntityClass(), id);
		if (existing == null)
			throw new ResourceNotFoundException();

		em.remove(existing);
		em.flush();
	}
}
