import javax.annotation.processing.Processor;

module com.snaphop.staticmustache.apt {
	
	requires java.annotation;
	requires java.compiler;
	//requires static metainf.services;
	requires org.jspecify;
	requires com.snaphop.staticmustache;
	
	
	provides Processor with com.snaphop.staticmustache.apt.GenerateRenderableAdapterProcessor;
}