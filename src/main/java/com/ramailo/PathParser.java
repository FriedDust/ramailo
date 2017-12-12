package com.ramailo;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import com.ramailo.service.ResourceService;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class PathParser {

	@Inject
	private ResourceService resourceService;

	public ResourceMeta parse(UriInfo uriInfo) {
		List<PathSegment> segments = uriInfo.getPathSegments();
		ResourceMeta resourceMeta = new ResourceMeta();

		if (segments.size() > 0) {
			resourceMeta.setResource(segments.get(0).toString());

			Class<?> entityClass = resourceService.findResourceEntity(resourceMeta.getResource());
			resourceMeta.setEntityClass(entityClass);
		}

		if (segments.size() > 1) {
			resourceMeta.setResourceId(segments.get(1).toString());
		}

		return resourceMeta;
	}

	public ResourceMeta parseMeta(String resourceName) {
		ResourceMeta resourceMeta = new ResourceMeta();
		resourceMeta.setResource(resourceName);

		Class<?> entityClass = resourceService.findResourceEntity(resourceMeta.getResource());
		resourceMeta.setEntityClass(entityClass);

		return resourceMeta;
	}

}
