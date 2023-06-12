/**
 * Spring Web MVC starter for JStachio:
 * <a href="https://repo1.maven.org/maven2/io/jstach/jstachio-spring-example/maven-metadata.xml" class="gav">io.jstach:jstachio-spring-example</a>.
 * 
 * @author agentgt
 */
module io.jstach.opt.spring.boot.webmvc {
	requires transitive io.jstach.opt.spring.webmvc;
	requires transitive io.jstach.opt.jmustache;

	requires static spring.jcl;
	requires transitive jakarta.servlet;
	
	requires spring.web;
	requires spring.webmvc;
	requires spring.beans;
	requires spring.core;
	requires spring.context;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	
	requires com.fasterxml.jackson.databind;

	opens io.jstach.opt.spring.boot.webmvc to //
	    spring.core, spring.web, spring.beans, spring.context
	;

    exports io.jstach.opt.spring.boot.webmvc;

}
