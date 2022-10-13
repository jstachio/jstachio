import io.jstach.spi.RenderService;

module io.jstach {
	exports io.jstach;
	exports io.jstach.spi;

	exports io.jstach.text;
	exports io.jstach.text.formats;

	requires static org.eclipse.jdt.annotation;
	
	uses RenderService;
	
	
}