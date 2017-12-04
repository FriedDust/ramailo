package com.ramailo.rs;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.ramailo.PathParser;
import com.ramailo.ResourceMeta;
import com.ramailo.meta.Resource;
import com.ramailo.service.MetaService;
import com.ramailo.service.ResourceService;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Path("/meta/")
public class MetaRs {

	@Inject
	private PathParser pathParser;

	@Inject
	private ResourceService resourceService;

	@Context
	private UriInfo uriInfo;

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response indexAction() {
		Set<Resource> resources = resourceService.findResources();
		return Response.ok().entity(resources).build();
	}

	@GET
	@Path("/{resource}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAction() {
		String resourceName = uriInfo.getPathSegments().get(1).toString();
		ResourceMeta resourceMeta = pathParser.parseMeta(resourceName);
		MetaService metaService = new MetaService(resourceMeta.getEntityClass());
		Resource resource = metaService.read();

		return Response.ok().entity(resource).build();
	}
}
