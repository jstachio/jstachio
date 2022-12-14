import org.springframework.stereotype.Component;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstache.JStacheName;
import io.jstach.jstache.JStachePath;
import io.jstach.opt.spring.webmvc.JStachioModelView;
import io.jstach.opt.spring.webmvc.ViewFactory;

/**
 * Spring example app for JStachio.
 * This module is an example modularized Spring Boot application.
 * <p>
 * <strong>
 * Make sure to take note of the annotations on this module as they define the jstachio config
 * needed to integrate with Spring.
 * </strong>
 * <p>
 * Next checkout the projects main package {@link io.jstach.opt.spring.example}.
 * <p>
 * This project uses the {@link io.jstach.opt.jmustache jmustache extension} which will
 * allow editing of mustache template files while the application is loaded 
 * (e.g. <code>spring-boot:run</code>) without recompiling.
 * <p>
 * <em>
 * While the code is Javadoc and the source is linked (if you click on the classes the source code is shown) 
 * it might be easier to look at the source 
 * <a href="https://github.com/jstachio/jstachio/tree/main/opt/jstachio-spring-example">directly on github.</a> 
 * </em>
 * 
 * @author agentgt
 */
@JStachePath(prefix = "views/", suffix = ".mustache") //
@JStacheInterfaces(
		templateAnnotations = {Component.class}, //
		templateImplements = {ViewFactory.class}, //
		modelImplements = {JStachioModelView.class}
)
@JStacheConfig(naming = @JStacheName(suffix="View"))
module io.jstach.opt.spring.example {
	requires transitive io.jstach.opt.spring;
	requires io.jstach.opt.jmustache;
	requires com.samskivert.jmustache;

	requires static spring.jcl;
	requires static jakarta.servlet;
	requires spring.web;
	requires spring.beans;
	requires spring.core;
	requires spring.context;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.webmvc;
	requires com.fasterxml.jackson.databind;
	
	opens io.jstach.opt.spring.example to //
	spring.core, spring.web, spring.beans, spring.context //
	, io.jstach.jstachio, io.jstach.opt.jmustache //
	, com.fasterxml.jackson.databind //
	;

	/*
	 * The following is just to make Javadoc for
	 * the example project. Not recommended you do
	 * this in a real world app.
	 */
	exports io.jstach.opt.spring.example;

}