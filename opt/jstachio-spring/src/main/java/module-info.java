/**
 * JStachio Spring integration module.
 * <p>
 * Current supported Spring is version 6 or greater.
 * @author agentgt
 */
module io.jstach.opt.spring {
	requires transitive io.jstach.jstachio;
	requires static spring.jcl;
	requires spring.web;
	requires spring.beans;
	requires spring.core;
	
	exports io.jstach.opt.spring;
}