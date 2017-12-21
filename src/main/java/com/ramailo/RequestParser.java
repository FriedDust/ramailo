package com.ramailo;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import com.ramailo.meta.Resource;
import com.ramailo.service.MetaServiceImpl;
import com.ramailo.service.ResourceServiceImpl;
import com.ramailo.util.QueryParamUtility;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class RequestParser {

	@Inject
	private ResourceServiceImpl resourceService;

	public RequestInfo parse(UriInfo uriInfo, String methodType) {
		List<PathSegment> segments = uriInfo.getPathSegments();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setMethodType(methodType);
		requestInfo.setQueryParams(
				QueryParamUtility.convert((MultivaluedMap<String, String>) uriInfo.getQueryParameters()));

		for (PathSegment ps : segments) {
			requestInfo.getPathParams().add(ps.toString());
		}

		if (segments.size() > 0) {
			String resourceName = segments.get(0).toString();
			Class<?> entityClass = resourceService.findResourceEntity(resourceName);
			requestInfo.setEntityClass(entityClass);

			MetaServiceImpl metaService = new MetaServiceImpl(entityClass);
			Resource resource = metaService.read();
			requestInfo.setResource(resource);
		}

		return requestInfo;
	}
}
