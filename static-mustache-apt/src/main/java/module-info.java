import javax.annotation.processing.Processor;

module com.snaphop.staticmustache.apt {
	
	requires java.compiler;
	//requires static metainf.services;
	requires com.snaphop.staticmustache;
	requires static org.eclipse.jdt.annotation;
	
	
	provides Processor with com.snaphop.staticmustache.apt.GenerateRenderableAdapterProcessor;
}