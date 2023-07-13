package io.jstach.opt.dropwizard.example;

import io.dropwizard.views.common.View;
import io.jstach.opt.dropwizard.JStacheViewSupport;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Dropwizard JAXRS example resource highlighting JStachio integration.
 *
 * @author agentgt
 *
 */
@Path("/example")
@Produces(MediaType.TEXT_HTML)
@SuppressWarnings("exports")
public class ExampleResource {

	/**
	 * For this endpoint we create the model and call the model to view mixin
	 * {@link JStacheViewSupport#toView()}. This approach allows us not to reference the
	 * generated template directly.
	 * @return jstachio powered view
	 */
	@GET
	public View hello() {
		return new ExampleModel("Hello world dropwizard using mixin").toView();
	}

	/**
	 * For this endpoint we use generated template directly and use a mixin method that
	 * will generate the view from the model.
	 * @return jstachio powered view
	 */
	@GET
	@Path("/template")
	public View template() {
		return ExampleModelRenderer.of().view(new ExampleModel("Hello dropwizard using template directly."));
	}

}
