
/**
 * Spring Webflux example app for JStachio:
 * <a href="https://repo1.maven.org/maven2/io/jstach/jstachio-spring-webflux-example/maven-metadata.xml" class="gav">io.jstach:jstachio-spring-webflux-example</a>.
 * 
 * This module is an example modularized Spring Boot Webflux application.
 * <p>
 * <strong>
 * Make sure to take note of the annotations on this module as they define the jstachio config
 * needed to integrate with Spring.
 * </strong>
 * <p>
 * Next checkout the projects main package {@link io.jstach.opt.spring.webflux.example}.
 * <p>
 * This project uses the {@linkplain io.jstach.opt.jmustache jmustache extension} which will
 * allow editing of mustache template files while the application is loaded 
 * (e.g. <code>spring-boot:run</code>) without recompiling.
 * <p>
 * <em>
 * While the code is Javadoc and the source is linked (if you click on the classes the source code is shown) 
 * it might be easier to look at the source 
 * <a href="https://github.com/jstachio/jstachio/tree/main/opt/jstachio-spring-webflux-example">directly on github.</a> 
 * </em>
 * 
 * @author agentgt
 */
//@JStachePath(prefix = "views/", suffix = ".mustache") //
module io.jstach.opt.spring.webflux.example {
	
	requires transitive io.jstach.opt.spring.webflux;
	
	requires io.jstach.opt.jmustache;

	requires static spring.jcl;
	
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires com.fasterxml.jackson.databind;
	
	opens io.jstach.opt.spring.webflux.example to //
	spring.core, spring.web, spring.beans, spring.context
	;

	opens io.jstach.opt.spring.webflux.example.message to //
	spring.core, spring.web, spring.beans, spring.context //
	, io.jstach.jstachio, io.jstach.opt.jmustache
	;

	opens io.jstach.opt.spring.webflux.example.hello to //
	spring.core, spring.web, spring.beans, spring.context //
	, io.jstach.jstachio, io.jstach.opt.jmustache //
	, com.fasterxml.jackson.databind //
	;

	/*
	 * The following is just to make Javadoc for
	 * the example project. Not recommended you do
	 * this in a real world app.
	 */
	exports io.jstach.opt.spring.webflux.example;
	exports io.jstach.opt.spring.webflux.example.hello;
	exports io.jstach.opt.spring.webflux.example.message;



}
