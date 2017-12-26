package com.ramailo.service;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import com.ramailo.annotation.RamailoResource;
import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.meta.Resource;
import com.ramailo.util.ClassFinder;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class ResourceServiceImpl {

	@Inject
	private ClassFinder classFinder;

	public Set<Resource> findResources() {
		Set<Resource> resources = new HashSet<>();

		Set<Class<?>> classes = findAll();
		for (Class<?> cls : classes) {
			Resource resource = new MetaBuilder(cls).read();
			resources.add(resource);
		}

		return resources;
	}

	public Set<Class<?>> findAll() {
		return classFinder.findHavingAnnotation(RamailoResource.class);
	}

	public Class<?> findResourceEntity(String resourceName) {
		Set<Class<?>> classes = findAll();
		for (Class<?> cls : classes) {
			RamailoResource annotation = cls.getAnnotation(RamailoResource.class);

			if (annotation.value().equals(resourceName)) {
				return cls;
			}
		}
		throw new ResourceNotFoundException();
	}
}
