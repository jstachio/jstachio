/**
 * Prisms are because we cannot load the annotation classes or the api classes
 * in the annotation processor because of potential class loading issues.
 * 
 * This project will get shaded into the actually annotation processor jar.
 * 
 * @author agentgt
 *
 */
module io.jstach.apt.prism {

	exports io.jstach.apt.prism;

	requires java.compiler;

	requires static org.eclipse.jdt.annotation;
	requires static io.jstach.jstache;
	requires static io.jstach.prism;
}