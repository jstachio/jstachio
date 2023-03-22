/**
 * JStachio Spring integration module:
 * <a href="https://repo1.maven.org/maven2/io/jstach/jstachio-spring/maven-metadata.xml" class="gav">io.jstach:jstachio-spring</a>.
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
 *  
 * @author agentgt
 */
module io.jstach.opt.spring {
	requires transitive io.jstach.jstachio;
	requires static org.eclipse.jdt.annotation;
	requires static spring.jcl;
	
	/*
	 * For javadoc and not an actual dep
	 */
	requires static spring.webmvc;
	
	requires spring.web;
	requires spring.beans;
	requires spring.core;
	requires spring.context;
	
	exports io.jstach.opt.spring;
	exports io.jstach.opt.spring.web;

}