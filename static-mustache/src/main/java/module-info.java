import com.github.sviperll.staticmustache.spi.RenderService;

module com.snaphop.staticmustache {
	exports com.github.sviperll.staticmustache;
	exports com.github.sviperll.staticmustache.spi;

	exports com.github.sviperll.staticmustache.text;
	exports com.github.sviperll.staticmustache.text.formats;

	
	requires java.annotation;
	requires org.jspecify;
	
	uses RenderService;
	
	
}