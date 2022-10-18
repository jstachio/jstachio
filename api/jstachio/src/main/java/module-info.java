import io.jstach.spi.JStachServices;

module io.jstach {
	exports io.jstach;
	exports io.jstach.spi;
	exports io.jstach.context;
	exports io.jstach.escapers;

	requires transitive io.jstach.annotation;
	
	requires static org.eclipse.jdt.annotation;
	
	uses JStachServices;
	
	
}