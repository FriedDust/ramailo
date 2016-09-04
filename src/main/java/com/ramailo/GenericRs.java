package com.ramailo;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
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

	// @Inject
	private PathParser pathParser = new PathParser();

	// @Inject
	private Processor processor = new Processor();

	@Inject
	private GenericService genericService;

	@GET
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAction() {
		Class<?> clazz = findResource();
		List<?> result = genericService.findAll(clazz);

		return Response.ok().entity(result).build();
	}

	private Class<?> findResource() {
		// ResourceMeta resource = pathParser.parse(uriInfo);
		// Class<?> clazz = processor.process(resource);
		//
		// return clazz;
		try {
			return Class.forName("com.frieddust.ramailodemo.entity.Customer");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException();
		}
	}
}
