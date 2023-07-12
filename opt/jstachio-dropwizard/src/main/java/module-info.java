import io.jstach.opt.dropwizard.JStachioViewRenderer;

/**
 * Dropwizard support
 * @author agent
 * @provides io.dropwizard.views.common.ViewRenderer JStachio view renderer
 */
module io.jstach.opt.dropwizard {
	requires transitive io.jstach.jstachio;
	requires io.dropwizard.views;
	requires static org.eclipse.jdt.annotation;
	exports io.jstach.opt.dropwizard;
	
	provides io.dropwizard.views.common.ViewRenderer with JStachioViewRenderer;
}