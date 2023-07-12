package io.jstach.opt.dropwizard.example;

import io.dropwizard.views.common.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/example")
@Produces(MediaType.TEXT_HTML)
public class ExampleResource {

	@GET
	public View hello() {
		return new ExampleModel("Hello world dropwizard using mixin").toView();
	}

}
