package io.jstach.apt.prism;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * THIS CLASS IS GENERATED FROM PrismsTest. Run the test and copy and paste.
 *
 * Prisms are because we cannot load the annotation classes or the api classes in the
 * annotation processor because of potential class loading issues.
 *
 * @author agentgt
 *
 */
public interface Prisms {

	public static final String IMPLEMENTATION_SUFFIX = "Renderer";

	@NonNullByDefault
	public enum Flag {

		DEBUG, //
		NO_INVERTED_BROKEN_CHAIN, //

	}

	/* API classes */
	public static final String RENDERER_CLASS = "io.jstach.jstachio.Renderer";

	public static final String TEMPLATE_CLASS = "io.jstach.jstachio.Template";

	public static final String TEMPLATE_PROVIDER_CLASS = "io.jstach.jstachio.spi.TemplateProvider";

	public static final String APPENDER_CLASS = "io.jstach.jstachio.Appender";

	public static final String ESCAPER_CLASS = "io.jstach.jstachio.Escaper";

	public static final String FORMATTER_CLASS = "io.jstach.jstachio.Formatter";

	public static final String DEFAULT_FORMATTER_CLASS = "io.jstach.jstachio.formatters.DefaultFormatter";

	public static final String TEMPLATE_INFO_CLASS = "io.jstach.jstachio.TemplateInfo";

	public static final String JSTACHE_SERVICES_CLASS = "io.jstach.jstachio.spi.JStacheServices";

	public static final String CONTEXT_NODE_CLASS = "io.jstach.jstachio.context.ContextNode";

	public static final String AUTO_FORMATTER_CLASS = "io.jstach.jstache.JStacheFormatter.AutoFormatter";

	public static final String AUTO_CONTENT_TYPE_CLASS = "io.jstach.jstache.JStacheContentType.AutoContentType";

	public static final String HTML_CLASS = "io.jstach.jstachio.escapers.Html";

	public static final String PLAIN_TEXT_CLASS = "io.jstach.jstachio.escapers.PlainText";

	/* Annotation classes */
	public static final String JSTACHES_CLASS = "io.jstach.jstache.JStaches";

	public static final String JSTACHE_CLASS = "io.jstach.jstache.JStache";

	public static final String JSTACHE_PATH_CLASS = "io.jstach.jstache.JStachePath";

	public static final String JSTACHE_INTERFACES_CLASS = "io.jstach.jstache.JStacheInterfaces";

	public static final String JSTACHE_PARTIALS_CLASS = "io.jstach.jstache.JStachePartials";

	public static final String JSTACHE_PARTIAL_CLASS = "io.jstach.jstache.JStachePartial";

	public static final String JSTACHE_LAMBDA_CLASS = "io.jstach.jstache.JStacheLambda";

	public static final String RAW_CLASS = "io.jstach.jstache.JStacheLambda.Raw";

	public static final String JSTACHE_CONTENT_TYPE_CLASS = "io.jstach.jstache.JStacheContentType";

	public static final String JSTACHE_FORMATTER_CLASS = "io.jstach.jstache.JStacheFormatter";

	public static final String JSTACHE_FORMATTER_TYPES_CLASS = "io.jstach.jstache.JStacheFormatterTypes";

	public static final String JSTACHE_FLAGS_CLASS = "io.jstach.jstache.JStacheFlags";

	public static final List<String> ANNOTATIONS = List.of( //
			JSTACHES_CLASS, //
			JSTACHE_CLASS, //
			JSTACHE_PATH_CLASS, //
			JSTACHE_INTERFACES_CLASS, //
			JSTACHE_PARTIALS_CLASS, //
			JSTACHE_PARTIAL_CLASS, //
			JSTACHE_LAMBDA_CLASS, //
			RAW_CLASS, //
			JSTACHE_CONTENT_TYPE_CLASS, //
			JSTACHE_FORMATTER_CLASS, //
			JSTACHE_FORMATTER_TYPES_CLASS, //
			JSTACHE_FLAGS_CLASS //
	);

}
