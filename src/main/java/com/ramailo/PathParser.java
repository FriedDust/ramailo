package com.ramailo;

import java.util.List;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class PathParser {
	
	public ResourceMeta parse(UriInfo uriInfo) {
		List<PathSegment> segments = uriInfo.getPathSegments();
		ResourceMeta resourceMeta = new ResourceMeta();
		
		if (segments.size() > 0) {
			resourceMeta.setEntity(segments.get(0).toString());
		}
		
		if (segments.size() > 1) {
			resourceMeta.setId(segments.get(1).toString());
		}
		
		return resourceMeta;
	}
}
