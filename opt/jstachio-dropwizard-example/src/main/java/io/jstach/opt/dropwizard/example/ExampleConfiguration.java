package io.jstach.opt.dropwizard.example;

import io.dropwizard.core.Configuration;
import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstache.JStachePath;
import io.jstach.opt.dropwizard.JStachioViewSupport;

/**
 * We use dropwizards configuration class to also register JStachio static configuration.
 * <strong>See the annotations on this class!</strong> The package-info then pulls in this
 * JStacheConfig so make sure to look at it as well.
 *
 * @author agentgt
 */
@JStacheConfig(pathing = @JStachePath(suffix = ".mustache"),
		interfacing = @JStacheInterfaces(modelImplements = JStachioViewSupport.class))
public class ExampleConfiguration extends Configuration {

}
