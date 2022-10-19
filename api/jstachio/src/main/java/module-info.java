import io.jstach.Renderer;
import io.jstach.spi.JStacheServices;

module io.jstach {
	exports io.jstach;
	exports io.jstach.spi;
	exports io.jstach.context;
	exports io.jstach.escapers;

	requires transitive io.jstach.annotation;
	
	requires static org.eclipse.jdt.annotation;
	
	uses JStacheServices;
	
	uses Renderer;
}