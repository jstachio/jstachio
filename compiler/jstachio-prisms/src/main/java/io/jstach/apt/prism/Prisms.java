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

	/**
	 * Generated
	 */
	public static final String JSTACHE_RESOURCES_PATH_OPTION = "jstache.resourcesPath";

	/**
	 * Generated
	 */
	public static final String JSTACHE_NAME_UNSPECIFIED = "*";

	/**
	 * Generated
	 */
	public static final String JSTACHE_NAME_DEFAULT_PREFIX = "";

	/**
	 * Generated
	 */
	public static final String JSTACHE_NAME_DEFAULT_SUFFIX = "Renderer";

	/**
	 * Generated
	 */
	public static final String JSTACHE_LAMBDA_SECTION_PARTIAL_NAME = "@section";

	/**
	 * Generated
	 */
	public static final String JSTACHE_FLAGS_UNSPECIFIED = "jstache.unspecified";

	/**
	 * Generated
	 */
	public static final String JSTACHE_FLAGS_DEBUG = "jstache.debug";

	/**
	 * Generated
	 */
	public static final String JSTACHE_FLAGS_NO_INVERTED_BROKEN_CHAIN = "jstache.no_inverted_broken_chain";

	/**
	 * Generated
	 */
	public static final String JSTACHE_FLAGS_NO_NULL_CHECKING = "jstache.no_null_checking";

	/**
	 * Generated
	 */
	public static final String JSTACHE_FLAGS_PRE_ENCODE_DISABLE = "jstache.pre_encode_disable";

	/**
	 * Generated
	 */
	@NonNullByDefault
	public enum Flag {

		/**
		 * Generated
		 */
		UNSPECIFIED, //
		/**
		 * Generated
		 */
		DEBUG, //
		/**
		 * Generated
		 */
		NO_INVERTED_BROKEN_CHAIN, //
		/**
		 * Generated
		 */
		NO_NULL_CHECKING, //
		/**
		 * Generated
		 */
		PRE_ENCODE_DISABLE, //

	}

	/**
	 * Generated
	 */
	@NonNullByDefault
	public enum JStacheType {

		/**
		 * Generated
		 */
		UNSPECIFIED, //
		/**
		 * Generated
		 */
		JSTACHIO, //
		/**
		 * Generated
		 */
		STACHE, //

	}

	/**
	 * Generated
	 */
	@NonNullByDefault
	public enum CatalogFlag {

		/**
		 * Generated
		 */
		GENERATE_PROVIDER_META_INF_SERVICE, //
		/**
		 * Generated
		 */
		GENERATE_FINDER_META_INF_SERVICE, //

	}

	/* API classes */
	/**
	 * Generated
	 */
	public static final String RENDERER_CLASS = "io.jstach.jstachio.Renderer";

	/**
	 * Generated
	 */
	public static final String OUTPUT_CLASS = "io.jstach.jstachio.Output";

	/**
	 * Generated
	 */
	public static final String ENCODED_OUTPUT_CLASS = "io.jstach.jstachio.Output.EncodedOutput";

	/**
	 * Generated
	 */
	public static final String TEMPLATE_CLASS = "io.jstach.jstachio.Template";

	/**
	 * Generated
	 */
	public static final String ENCODED_TEMPLATE_CLASS = "io.jstach.jstachio.Template.EncodedTemplate";

	/**
	 * Generated
	 */
	public static final String TEMPLATE_PROVIDER_CLASS = "io.jstach.jstachio.spi.TemplateProvider";

	/**
	 * Generated
	 */
	public static final String GENERATED_TEMPLATE_PROVIDER_CLASS = "io.jstach.jstachio.spi.TemplateProvider.GeneratedTemplateProvider";

	/**
	 * Generated
	 */
	public static final String APPENDER_CLASS = "io.jstach.jstachio.Appender";

	/**
	 * Generated
	 */
	public static final String ESCAPER_CLASS = "io.jstach.jstachio.Escaper";

	/**
	 * Generated
	 */
	public static final String FORMATTER_CLASS = "io.jstach.jstachio.Formatter";

	/**
	 * Generated
	 */
	public static final String DEFAULT_FORMATTER_CLASS = "io.jstach.jstachio.formatters.DefaultFormatter";

	/**
	 * Generated
	 */
	public static final String TEMPLATE_INFO_CLASS = "io.jstach.jstachio.TemplateInfo";

	/**
	 * Generated
	 */
	public static final String TEMPLATE_CONFIG_CLASS = "io.jstach.jstachio.TemplateConfig";

	/**
	 * Generated
	 */
	public static final String FILTER_CHAIN_CLASS = "io.jstach.jstachio.spi.JStachioFilter.FilterChain";

	/**
	 * Generated
	 */
	public static final String JSTACHIO_EXTENSION_CLASS = "io.jstach.jstachio.spi.JStachioExtension";

	/**
	 * Generated
	 */
	public static final String CONTEXT_NODE_CLASS = "io.jstach.jstachio.context.ContextNode";

	/**
	 * Generated
	 */
	public static final String UNSPECIFIED_FORMATTER_CLASS = "io.jstach.jstache.JStacheFormatter.UnspecifiedFormatter";

	/**
	 * Generated
	 */
	public static final String UNSPECIFIED_CONTENT_TYPE_CLASS = "io.jstach.jstache.JStacheContentType.UnspecifiedContentType";

	/**
	 * Generated
	 */
	public static final String HTML_CLASS = "io.jstach.jstachio.escapers.Html";

	/**
	 * Generated
	 */
	public static final String PLAIN_TEXT_CLASS = "io.jstach.jstachio.escapers.PlainText";

	/* Annotation classes */
	/**
	 * Generated
	 */
	public static final String JSTACHE_CLASS = "io.jstach.jstache.JStache";

	/**
	 * Generated
	 */
	public static final String JSTACHE_CATALOG_CLASS = "io.jstach.jstache.JStacheCatalog";

	/**
	 * Generated
	 */
	public static final String JSTACHE_CONFIG_CLASS = "io.jstach.jstache.JStacheConfig";

	/**
	 * Generated
	 */
	public static final String JSTACHE_NAME_CLASS = "io.jstach.jstache.JStacheName";

	/**
	 * Generated
	 */
	public static final String JSTACHE_PATH_CLASS = "io.jstach.jstache.JStachePath";

	/**
	 * Generated
	 */
	public static final String JSTACHE_INTERFACES_CLASS = "io.jstach.jstache.JStacheInterfaces";

	/**
	 * Generated
	 */
	public static final String JSTACHE_PARTIALS_CLASS = "io.jstach.jstache.JStachePartials";

	/**
	 * Generated
	 */
	public static final String JSTACHE_PARTIAL_CLASS = "io.jstach.jstache.JStachePartial";

	/**
	 * Generated
	 */
	public static final String JSTACHE_LAMBDA_CLASS = "io.jstach.jstache.JStacheLambda";

	/**
	 * Generated
	 */
	public static final String RAW_CLASS = "io.jstach.jstache.JStacheLambda.Raw";

	/**
	 * Generated
	 */
	public static final String JSTACHE_CONTENT_TYPE_CLASS = "io.jstach.jstache.JStacheContentType";

	/**
	 * Generated
	 */
	public static final String JSTACHE_FORMATTER_CLASS = "io.jstach.jstache.JStacheFormatter";

	/**
	 * Generated
	 */
	public static final String JSTACHE_FORMATTER_TYPES_CLASS = "io.jstach.jstache.JStacheFormatterTypes";

	/**
	 * Generated
	 */
	public static final String JSTACHE_FLAGS_CLASS = "io.jstach.jstache.JStacheFlags";

	/**
	 * Generated
	 */
	public static final List<String> ANNOTATIONS = List.of( //
			JSTACHE_CLASS, //
			JSTACHE_CATALOG_CLASS, //
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
