/**
 * Dropwizard support
 * @author agent
 *
 */
module io.jstach.opt.dropwizard {
	requires transitive io.jstach.jstachio;
	requires io.dropwizard.views;
	requires static org.kohsuke.metainf_services;
	requires static org.eclipse.jdt.annotation;
	exports io.jstach.opt.dropwizard;
}