/**
 * JStachio Core Runtime API
 * <p>
 * By default JStachio generates code that needs this module however libraries that
 * use models (classes annotated with io.jstach.annotation.JStache) 
 * without needing rendering do not necessarily need this module.
 * <p>
 * Furthermore it might be possible to generate code that does not need the runtime in the future.
 */
module io.jstach {
	exports io.jstach;
	exports io.jstach.spi;
	exports io.jstach.context;
	exports io.jstach.escapers;
	exports io.jstach.formatters;


	requires transitive io.jstach.annotation;
	
	requires static org.eclipse.jdt.annotation;
	
	uses io.jstach.spi.JStacheServices;
}