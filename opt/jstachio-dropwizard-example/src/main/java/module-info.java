/**
 * 
 * Dropwizard example app using JStachio:
 * <a href="https://repo1.maven.org/maven2/io/jstach/jstachio-dropwizard-example/maven-metadata.xml" class="gav">io.jstach:jstachio-dropwizard-example</a>.
 * 
 * This module is an example modularized Dropwizard application.
 * It is not a requirement that your application be modularized to use JStachio (or Dropwizard w/ JStachio).
 * Dropwizard is still not really designed for modularization so this module-info looks
 * more complex than it really should.
 * <strong>Also this application is not builtin as an Uberjar for maven central deployment reasons.</strong>
 * <p>
 * <em>
 * While the code is Javadoc and the source is linked (if you click on the classes the source code is shown) 
 * it might be easier to look at the source 
 * <a href="https://github.com/jstachio/jstachio/tree/main/opt/jstachio-dropwizard-example">directly on github.</a> 
 * </em>
 * 
 * @apiNote This module is not public API as it is just an example and thus does not follow semver policy!
 * @author agentgt
 *
 */
module io.jstach.opt.dropwizard.example {
	
	/*
	 * We have to open the package up because so much of dropwizard
	 * relies on reflection.
	 */
	opens io.jstach.opt.dropwizard.example;
	exports io.jstach.opt.dropwizard.example;
	
	requires io.jstach.opt.dropwizard;
	requires io.dropwizard.core;
	requires io.dropwizard.views;
	requires io.dropwizard.health;
	requires io.dropwizard.configuration;
	requires io.dropwizard.metrics;
	requires io.dropwizard.jersey;
	
	requires com.codahale.metrics.health;
	requires com.codahale.metrics;
	requires ch.qos.logback.classic;
	
	requires jakarta.ws.rs;
	
	requires static org.eclipse.jdt.annotation;
}