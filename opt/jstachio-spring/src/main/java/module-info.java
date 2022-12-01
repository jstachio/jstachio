/**
 * JStachio Spring integration module.
 * <p>
 * Current supported Spring is version 6 or greater.
 * @author agentgt
 */
module io.jstach.opt.spring {
	requires transitive io.jstach.jstachio;
	requires static spring.jcl;
	requires transitive spring.web;
	requires spring.beans;
	requires transitive spring.core;
	
	exports io.jstach.opt.spring;
}