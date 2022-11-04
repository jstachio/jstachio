
/**
 * JStachio Core Runtime API.
 * <p>
 * The main entry point is {@link io.jstach.JStachio} 
 * which provides various reflection based lookup
 * mechanisms of models which can be useful if you do not want direct references to generated
 * code in your code base as well as applies runtime logic like filtering.
 * <p>
 * This module also provides runtime extension points via the {@link java.util.ServiceLoader} and
 * the SPI of {@link io.jstach.spi.JStacheServices}
 * <p>
 * By default JStachio generates code that needs this module however libraries that
 * use models (classes annotated with {@link io.jstach.annotation.JStache}) 
 * without needing rendering do not necessarily need this module.
 * Furthermore it might be possible to generate code that does not need the runtime in the future.
 * 
 * @see io.jstach.JStachio
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
	uses io.jstach.spi.RendererProvider;
}