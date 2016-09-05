package com.ramailo.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.ramailo.RamailoResource;
import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.meta.Resource;
import com.ramailo.util.ClassFinder;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class ResourceService {

	@Inject
	private ClassFinder classFinder;

	public List<Resource> findResources() {
		List<Resource> resources = new ArrayList<>();

		List<Class<?>> classes = findAll();
		for (Class<?> cls : classes) {
			MetaService metaService = new MetaService(cls);
			Resource resource = metaService.read();

			resources.add(resource);
		}

		return resources;
	}

	public List<Class<?>> findAll() {
		return classFinder.findHavingAnnotation(RamailoResource.class);
	}

	public Class<?> findResourceEntity(String resourceName) {
		List<Class<?>> classes = findAll();
		for (Class<?> cls : classes) {
			RamailoResource annotation = cls.getAnnotation(RamailoResource.class);

			if (annotation.value().equals(resourceName)) {
				return cls;
			}
		}
		throw new ResourceNotFoundException();
	}
}
