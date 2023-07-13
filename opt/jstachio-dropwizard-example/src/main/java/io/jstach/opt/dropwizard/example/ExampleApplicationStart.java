package io.jstach.opt.dropwizard.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.views.common.ViewBundle;

/**
 * Dropwizard example application entry point.
 *
 * @author agentgt
 *
 */
public class ExampleApplicationStart extends Application<ExampleConfiguration> {

	/**
	 * Main method entrypoint for the application. If no args are passed we assume that
	 * "<code>server</code>" is the desired mode.
	 * @param args if empty <code>["server"]</code> will be used.
	 * @throws Exception if the application fails to start
	 */
	public static void main(String[] args) throws Exception {
		String[] resolved = args;
		if (resolved.length == 0) {
			resolved = new String[] { "server" };
		}
		new ExampleApplicationStart().run(resolved);
	}

	@Override
	public void run(ExampleConfiguration configuration, @SuppressWarnings("exports") Environment environment)
			throws Exception {
		/*
		 * This health check is just to make dropwizard happy
		 */
		environment.healthChecks().register("template", new TemplateHealthCheck("JSTACHIO"));
		environment.jersey().register(new ExampleResource());
	}

	@Override
	public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
		/*
		 * We could explicitly add JStachioViewRenderer here but it will be picked up by
		 * the sevice loader.
		 */
		bootstrap.addBundle(new ViewBundle<>());
	}

}
