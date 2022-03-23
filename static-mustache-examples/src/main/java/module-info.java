import javax.annotation.processing.Processor;

module com.github.sviperll.staticmustache.example {
	requires com.github.sviperll.staticmustache;
	requires java.annotation;
	requires java.compiler;
	
	uses Processor;
}