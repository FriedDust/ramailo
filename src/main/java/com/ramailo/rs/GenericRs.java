package com.ramailo.rs;

import java.util.List;
import java.util.stream.Collectors;

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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.ramailo.PathParser;
import com.ramailo.ResourceMeta;
import com.ramailo.meta.Action;
import com.ramailo.meta.Resource;
import com.ramailo.service.GenericService;
import com.ramailo.service.MetaService;
import com.ramailo.util.QueryParamUtility;
import com.ramailo.util.QueryParamUtility.QueryParam;

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
	private GenericService genericService;

	@GET
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAction() {
		try {
			List<QueryParam> queryParams = QueryParamUtility
					.convert((MultivaluedMap<String, String>) uriInfo.getQueryParameters());
			ResourceMeta resourceMeta = pathParser.parse(uriInfo);
			MetaService metaService = new MetaService(resourceMeta.getEntityClass());
			Resource resource = metaService.read();
			Action staticAction = null;
			Action action = null;

			if (resourceMeta.getFirstPathParam() != null) {
				List<Action> matches = resource.getStaticActions().stream()
						.filter(act -> act.getPathName().equals(resourceMeta.getFirstPathParam()))
						.collect(Collectors.toList());

				if (matches.size() > 0) {
					staticAction = matches.get(0);
				}
			}
			if (resourceMeta.getSecondPathParam() != null) {
				List<Action> matches = resource.getActions().stream()
						.filter(act -> act.getPathName().equals(resourceMeta.getSecondPathParam()))
						.collect(Collectors.toList());

				if (matches.size() > 0) {
					action = matches.get(0);
				}
			}

			if (staticAction != null) {
				Object result = genericService.invokeStaticAction(resourceMeta, staticAction, queryParams);
				return Response.ok().entity(result).build();
			}

			if (action != null) {
				Object result = genericService.invokeAction(resourceMeta, action, queryParams);
				return Response.ok().entity(result).build();
			}

			if (resourceMeta.getFirstPathParam() != null) {
				Object result = genericService.findById(resourceMeta);
				return Response.ok().entity(result).build();
			}

			List<?> result = genericService.find(resourceMeta,
					QueryParamUtility.convert((MultivaluedMap<String, String>) uriInfo.getQueryParameters()));

			return Response.ok().entity(result).build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@POST
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postAction(JsonObject object) {
		ResourceMeta resource = pathParser.parse(uriInfo);
		Object result = genericService.create(resource, object);

		return Response.ok().entity(result).build();
	}

	@PUT
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putAction(JsonObject object) {
		ResourceMeta resource = pathParser.parse(uriInfo);
		Object result = genericService.update(resource, object);

		return Response.ok().entity(result).build();
	}

	@DELETE
	@Path("/{resource:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAction() {
		ResourceMeta resource = pathParser.parse(uriInfo);
		genericService.remove(resource);

		return Response.ok().build();
	}
}
