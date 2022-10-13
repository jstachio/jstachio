import com.github.sviperll.staticmustache.spi.RenderService;

module com.snaphop.staticmustache {
	exports com.github.sviperll.staticmustache;
	exports com.github.sviperll.staticmustache.spi;

	exports com.github.sviperll.staticmustache.text;
	exports com.github.sviperll.staticmustache.text.formats;

	requires static org.eclipse.jdt.annotation;
	
	uses RenderService;
	
	
}