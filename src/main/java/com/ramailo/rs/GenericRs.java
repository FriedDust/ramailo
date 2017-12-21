package com.ramailo.rs;

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

import com.ramailo.RequestInfo;
import com.ramailo.RequestParser;
import com.ramailo.service.GenericMiddleware;

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
	private RequestParser requestParser;

	@Inject
	private GenericMiddleware genericMiddleware;

	@GET
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAction() {
		try {
			RequestInfo requestInfo = requestParser.parse(uriInfo, "GET");
			Object result = genericMiddleware.processGetAction(requestInfo);

			return Response.ok().entity(result).build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@POST
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postAction(JsonObject body) {
		try {
			RequestInfo requestInfo = requestParser.parse(uriInfo, "POST");
			Object result = genericMiddleware.processPostAction(requestInfo, body);

			return Response.ok().entity(result).build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PUT
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putAction(JsonObject body) {
		try {
			RequestInfo requestInfo = requestParser.parse(uriInfo, "PUT");
			Object result = genericMiddleware.processPutAction(requestInfo, body);

			return Response.ok().entity(result).build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@DELETE
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAction() {
		try {
			RequestInfo requestInfo = requestParser.parse(uriInfo, "DELETE");
			genericMiddleware.processDeleteAction(requestInfo);

			return Response.ok().build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
