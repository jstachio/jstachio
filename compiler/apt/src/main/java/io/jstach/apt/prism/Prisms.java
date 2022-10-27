package io.jstach.apt.prism;

import java.util.List;

public interface Prisms {

	public enum Flag {

		DEBUG, NO_INVERTED_BROKEN_CHAIN

	}

	public static final String JSTACHES_CLASS = "io.jstach.annotation.JStaches";

	public static final String JSTACHE_CLASS = "io.jstach.annotation.JStache";

	public static final String JSTACHEPATH_CLASS = "io.jstach.annotation.JStachePath";

	public static final String JSTACHEINTERFACES_CLASS = "io.jstach.annotation.JStacheInterfaces";

	public static final String JSTACHEPARTIALS_CLASS = "io.jstach.annotation.JStachePartials";

	public static final String JSTACHEPARTIAL_CLASS = "io.jstach.annotation.JStachePartial";

	public static final String JSTACHELAMBDA_CLASS = "io.jstach.annotation.JStacheLambda";

	public static final String RAW_CLASS = "io.jstach.annotation.JStacheLambda.Raw";

	public static final String JSTACHECONTENTTYPE_CLASS = "io.jstach.annotation.JStacheContentType";

	public static final String JSTACHEFORMATTERTYPES_CLASS = "io.jstach.annotation.JStacheFormatterTypes";

	public static final String JSTACHEFLAGS_CLASS = "io.jstach.annotation.JStacheFlags";

	public static final List<String> ANNOTATIONS = List.of( //
			"io.jstach.annotation.JStaches", //
			"io.jstach.annotation.JStache", //
			"io.jstach.annotation.JStachePath", //
			"io.jstach.annotation.JStacheInterfaces", //
			"io.jstach.annotation.JStachePartials", //
			"io.jstach.annotation.JStachePartial", //
			"io.jstach.annotation.JStacheLambda", //
			"io.jstach.annotation.JStacheLambda.Raw", //
			"io.jstach.annotation.JStacheContentType", //
			"io.jstach.annotation.JStacheFormatterTypes", //
			"io.jstach.annotation.JStacheFlags" //
	);

	public static final String RENDERER_CLASS = "io.jstach.Renderer";

	public static final String APPENDER_CLASS = "io.jstach.Appender";

	public static final String ESCAPER_CLASS = "io.jstach.Escaper";

	public static final String FORMATTER_CLASS = "io.jstach.Formatter";

	public static final String RENDERFUNCTION_CLASS = "io.jstach.RenderFunction";

	public static final String RENDERABLE_CLASS = "io.jstach.Renderable";

	public static final String JSTACHESERVICES_CLASS = "io.jstach.spi.JStacheServices";

	public static final String CONTEXTNODE_CLASS = "io.jstach.context.ContextNode";

	public static final String AUTOCONTENTTYPE_CLASS = "io.jstach.annotation.JStache.AutoContentType";

	public static final String HTML_CLASS = "io.jstach.escapers.Html";

}
