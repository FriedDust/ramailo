package com.ramailo.rs;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ramailo.meta.Resource;
import com.ramailo.service.ResourceService;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Path("/meta/")
public class MetaRs {

	@Inject
	private ResourceService resourceService;

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response indexAction() {
		Set<Resource> resources = resourceService.findResources();
		return Response.ok().entity(resources).build();
	}
}
