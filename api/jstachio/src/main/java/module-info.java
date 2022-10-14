import io.jstach.spi.RenderService;

module io.jstach {
	exports io.jstach.spi;
	exports io.jstach.context;
	exports io.jstach.text;
	exports io.jstach.text.formats;

	requires transitive io.jstach.annotation;
	
	requires static org.eclipse.jdt.annotation;
	
	uses RenderService;
	
	
}