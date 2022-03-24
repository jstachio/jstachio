import javax.annotation.processing.Processor;

module com.snaphop.staticmustache.example {
	requires com.snaphop.staticmustache;
	requires java.annotation;
	requires java.compiler;
	
	uses Processor;
}