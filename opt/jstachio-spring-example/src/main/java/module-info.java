import org.springframework.stereotype.Component;

import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstache.JStachePath;

/**
 * Spring example app for JStachio
 * 
 * @author agentgt
 */
@JStachePath(prefix = "views/", suffix = ".mustache") //
@JStacheInterfaces(templateAnnotations = {Component.class})
module io.jstach.opt.spring.example {
	requires transitive io.jstach.opt.spring;
	requires io.jstach.opt.jmustache;
	requires com.samskivert.jmustache;

	requires static spring.jcl;
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

}