import javax.annotation.processing.Processor;

import io.jstach.spi.JStacheServices;
/**
 * Examples
 * 
 * @author agent
 *
 * @provides JStacheServices
 * @uses Processor
 * @uses JStacheServices
 */
module io.jstach.examples {
	requires transitive io.jstach;
	requires java.compiler;
	requires org.kohsuke.metainf_services;
	requires com.samskivert.jmustache;
	requires org.mapstruct;

	requires static org.eclipse.jdt.annotation;

	opens io.jstach.examples to com.samskivert.jmustache, io.jstach;

	exports io.jstach.examples to org.mapstruct;

	uses Processor;
	uses JStacheServices;
}