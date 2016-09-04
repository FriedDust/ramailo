package com.ramailo;

import java.util.Set;

import org.reflections.Reflections;

import com.ramailo.exception.ResourceNotFoundException;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class Processor {

	public Class<?> process(ResourceMeta resourceMeta) {
		Class<?> clazz = findResourceEntity(resourceMeta.getEntity());
		return clazz;
	}

	public Class<?> findResourceEntity(String resourceName) {
		Reflections reflections = new Reflections("");
		Set<Class<?>> resources = reflections.getTypesAnnotatedWith(RamailoResource.class);

		for (Class<?> cls : resources) {
			if (cls.getSimpleName().equals(resourceName)) {
				return cls;
			}
		}
		throw new ResourceNotFoundException();
	}

}
