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

	public static final String JSTACHE_RESOURCES_PATH_OPTION = "jstache.resourcesPath";

	public static final String JSTACHE_NAME_UNSPECIFIED = "*";

	public static final String JSTACHE_NAME_DEFAULT_PREFIX = "";

	public static final String JSTACHE_NAME_DEFAULT_SUFFIX = "Renderer";

	public static final String JSTACHE_FLAGS_DEBUG = "jstache.debug";

	public static final String JSTACHE_FLAGS_NO_INVERTED_BROKEN_CHAIN = "jstache.no_inverted_broken_chain";

	@NonNullByDefault
	public enum Flag {

		DEBUG, //
		NO_INVERTED_BROKEN_CHAIN, //

	}

	@NonNullByDefault
	public enum JStacheType {

		UNSPECIFIED, //
		JSTACHIO, //
		STACHE, //

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

	public static final String TEMPLATE_CONFIG_CLASS = "io.jstach.jstachio.TemplateConfig";

	public static final String FILTER_CHAIN_CLASS = "io.jstach.jstachio.spi.JStachioFilter.FilterChain";

	public static final String JSTACHIO_EXTENSION_CLASS = "io.jstach.jstachio.spi.JStachioExtension";

	public static final String CONTEXT_NODE_CLASS = "io.jstach.jstachio.context.ContextNode";

	public static final String UNSPECIFIED_FORMATTER_CLASS = "io.jstach.jstache.JStacheFormatter.UnspecifiedFormatter";

	public static final String UNSPECIFIED_CONTENT_TYPE_CLASS = "io.jstach.jstache.JStacheContentType.UnspecifiedContentType";

	public static final String HTML_CLASS = "io.jstach.jstachio.escapers.Html";

	public static final String PLAIN_TEXT_CLASS = "io.jstach.jstachio.escapers.PlainText";

	/* Annotation classes */
	public static final String JSTACHE_CLASS = "io.jstach.jstache.JStache";

	public static final String JSTACHE_CONFIG_CLASS = "io.jstach.jstache.JStacheConfig";

	public static final String JSTACHE_NAME_CLASS = "io.jstach.jstache.JStacheName";

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
			JSTACHE_CLASS, //
			JSTACHE_CONFIG_CLASS, //
			JSTACHE_NAME_CLASS, //
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
