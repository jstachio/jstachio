import javax.annotation.processing.Processor;

import io.jstach.jstachio.spi.JStachioServices;
module io.jstach.examples {
	requires transitive io.jstach.jstachio;
	requires java.compiler;
	requires org.kohsuke.metainf_services;
	requires com.samskivert.jmustache;
	requires org.mapstruct;

	requires static org.eclipse.jdt.annotation;

	opens io.jstach.examples to com.samskivert.jmustache, io.jstach.jstachio;

	exports io.jstach.examples to org.mapstruct;

	uses Processor;
	uses JStachioServices;
}