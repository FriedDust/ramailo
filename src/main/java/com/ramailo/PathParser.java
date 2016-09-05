package com.ramailo;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import com.ramailo.exception.ResourceNotFoundException;
import com.ramailo.util.ClassFinder;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class PathParser {

	@Inject
	private ClassFinder classFinder;
	
	public ResourceMeta parse(UriInfo uriInfo) {
		List<PathSegment> segments = uriInfo.getPathSegments();
		ResourceMeta resourceMeta = new ResourceMeta();
		
		if (segments.size() > 0) {
			resourceMeta.setResource(segments.get(0).toString());
			
			Class<?> entityClass = findResourceEntity(resourceMeta.getResource());
			resourceMeta.setEntityClass(entityClass);
		}
		
		if (segments.size() > 1) {
			resourceMeta.setId(segments.get(1).toString());
		}
		
		return resourceMeta;
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
