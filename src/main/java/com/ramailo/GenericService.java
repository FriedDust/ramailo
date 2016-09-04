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

	public List<?> findAll(Class<?> clazz) {
		String jpql = "select x from " + clazz.getSimpleName() + " x";
		List<?> result = em.createQuery(jpql).getResultList();

		return result;
	}

	public Object create(Class<?> clazz, JsonObject object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Object entity = mapper.readValue(object.toString(), clazz);
			em.persist(entity);
			em.flush();

			return entity;
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public Object update(Class<?> clazz, Object pk, JsonObject object) {
		ObjectMapper mapper = new ObjectMapper();
		Integer id = Integer.valueOf(pk.toString());
		try {
			Object entity = mapper.readValue(object.toString(), clazz);
			Object existing = em.find(clazz, id);
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

	public void remove(Class<?> clazz, Object pk) {
		Integer id = Integer.valueOf(pk.toString());
		Object existing = em.find(clazz, id);
		if (existing == null)
			throw new ResourceNotFoundException();

		em.remove(existing);
		em.flush();
	}
}
