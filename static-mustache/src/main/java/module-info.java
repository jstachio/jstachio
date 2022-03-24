import javax.annotation.processing.Processor;

module com.snaphop.staticmustache {
	exports com.github.sviperll.staticmustache;
	exports com.github.sviperll.staticmustache.text;
	exports com.github.sviperll.staticmustache.text.formats;

	
	requires java.annotation;
	requires java.compiler;
	//requires static metainf.services;
	requires org.jspecify;
	
	
	provides Processor with com.github.sviperll.staticmustache.GenerateRenderableAdapterProcessor;
}