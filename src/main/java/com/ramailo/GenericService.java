package com.ramailo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
//@Stateless
public class GenericService {

	//@Inject
	//private EntityManager em;
	
	public List<?> findAll(Class<?> clazz) {
//		String jpql = "select x from " + clazz.getSimpleName() + " x";
//		List<?> result = em.createQuery(jpql).getResultList();
//		
//		return result;
		return new ArrayList();
	}
}
