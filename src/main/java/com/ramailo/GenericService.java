package com.ramailo;

import java.io.IOException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;

import com.fasterxml.jackson.databind.ObjectMapper;

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

			return entity;
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
}
