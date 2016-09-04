package com.ramailo.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/wow/")
public class WowRs {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String wow() {
		return "wowowow";
	}
}
