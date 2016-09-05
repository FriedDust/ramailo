package com.ramailo;

import java.util.List;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Path("")
public class GenericRs {

	@Context
	private UriInfo uriInfo;

	@Inject
	private PathParser pathParser;

	@Inject
	private Processor processor;

	@Inject
	private GenericService genericService;

	@GET
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAction() {
		Class<?> clazz = findResource();
		List<?> result = genericService.findAll(clazz);

		return Response.ok().entity(result).build();
	}
	
	@POST
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postAction(JsonObject object) {
		
		Class<?> clazz = findResource();
		Object result = genericService.create(clazz, object);

		return Response.ok().entity(result).build();
	}
	
	@PUT
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putAction(JsonObject object) {
		ResourceMeta resource = pathParser.parse(uriInfo);
		
		Class<?> clazz = findResource();
		Object result = genericService.update(clazz, resource.getId(), object);

		return Response.ok().entity(result).build();
	}
	
	@DELETE
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteAction(JsonObject object) {
		ResourceMeta resource = pathParser.parse(uriInfo);
		
		Class<?> clazz = findResource();
		genericService.remove(clazz, resource.getId());

		return Response.ok().build();
	}

	private Class<?> findResource() {
		 ResourceMeta resource = pathParser.parse(uriInfo);
		 Class<?> clazz = processor.process(resource);
		
		 return clazz;
	}
}
