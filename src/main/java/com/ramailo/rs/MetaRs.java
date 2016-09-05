package com.ramailo.rs;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
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

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Path("/meta/")
public class MetaRs {

	@Inject
	private PathParser pathParser;

	@Context
	private UriInfo uriInfo;
	
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
