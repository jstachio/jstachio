/**
 * JStachio Spring webmvc module.
 * <p>
 * 
 * This module has support for various Spring web options. Since
 * the integrations are all in one module (jar) most of the dependencies are optional
 * (both maven and module-info) and thus dependencies will not be pulled in transitively.
 * 
 * <em>Current supported Spring is version 6.</em>
 * 
 * <h2>Config and template finding integration</h2>
 * <strong>See {@link io.jstach.opt.spring}</strong>
 * <p>
 * {@link io.jstach.opt.spring.SpringJStachioExtension} will use Spring Environment
 * abstraction for config and template finding. You may wire in other extensions such as
 * JMustache as well instead of relying on the ServiceLoader.

 * <h2>Web integration</h2>
 * <strong>See {@link io.jstach.opt.spring.web}</strong>
 * <p>
 * Notably {@link io.jstach.opt.spring.web.JStachioHttpMessageConverter} provides a unique way
 * to do MVC in a type-safe way instead of the normal {@code Map<String,String>} model and
 * {@link java.lang.String} view way (ModelAndView). This particularly integration
 * is not tied to the Servlet API.
 * 
 * <h2>Web MVC integration</h2>
 * <strong>See {@link io.jstach.opt.spring.webmvc}</strong>
 * <p>
 * {@link io.jstach.opt.spring.webmvc.JStachioModelView} allows you to construct
 * servlet based Spring Views for traditional Web MVC Spring applications.
 * This integration is tied to the servlet API and thus will need it as
 * a dependency.
 * 
 *  
 * @author agentgt
 */
module io.jstach.opt.spring.webmvc {
	
	requires transitive io.jstach.opt.spring;
	requires static org.eclipse.jdt.annotation;
	requires static spring.jcl;
	
	/*
	 * start required for Spring webmvc support
	 */
	requires spring.webmvc;
	requires static jakarta.servlet;
	/* end spring mvc optional deps*/
	

	exports io.jstach.opt.spring.webmvc;

}
