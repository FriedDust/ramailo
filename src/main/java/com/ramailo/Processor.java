package com.ramailo;

import java.util.List;

import javax.inject.Inject;

import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.util.ClassFinder;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class Processor {

	@Inject
	private ClassFinder classFinder;

	public Class<?> process(ResourceMeta resourceMeta) {
		Class<?> clazz = findResourceEntity(resourceMeta.getEntity());
		return clazz;
	}

	public Class<?> findResourceEntity(String resourceName) {
		List<Class<?>> classes = classFinder.findHavingAnnotation(RamailoResource.class);
		for (Class<?> cls : classes) {
			RamailoResource annotation = cls.getAnnotation(RamailoResource.class);

			if (annotation.value().equals(resourceName)) {
				return cls;
			}
		}
		throw new ResourceNotFoundException();
	}

}
