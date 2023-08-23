import javax.annotation.processing.Processor;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.jstachio.spi.TemplateProvider;

module io.jstach.examples {
	requires transitive io.jstach.jstachio;
	requires java.compiler;
	requires com.samskivert.jmustache;
	requires org.mapstruct;

	requires static org.eclipse.jdt.annotation;

	opens io.jstach.examples to com.samskivert.jmustache, io.jstach.jstachio;
	opens io.jstach.examples.reflect to io.jstach.jstachio;
	opens io.jstach.examples.i18n to io.jstach.jstachio;
	
	provides TemplateProvider with io.jstach.examples.finder.ExampleTemplateFinder;

	exports io.jstach.examples to org.mapstruct;

	uses Processor;
	uses JStachioExtension;
}