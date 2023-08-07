package io.jstach.opt.spring.webmvc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output.CloseableEncodedOutput;
import io.jstach.jstachio.context.ContextJStachio;
import io.jstach.jstachio.context.ContextNode;
import io.jstach.opt.spring.web.JStachioHttpMessageConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Another way to use JStachio with Spring MVC is to have models implement Springs
 * {@link View} interface. You can enforce that your models implement this interface with
 * {@link JStacheInterfaces}. Alternatively one can call {@link #of(Object)} on the model
 * and return the result.
 * <p>
 * This view will by default use the static jstachio singleton and if configured correctly
 * that will be the spring version.
 * <p>
 * This approach has pros and cons. It makes your models slightly coupled to Spring MVC
 * but allows you to return different views if say you had to redirect on some inputs
 * ({@link RedirectView}).
 *
 * @author agentgt
 *
 */
public interface JStachioModelView extends View {

	/**
	 * The default media type is "<code>text/html; charset=UTF-8</code>".
	 */
	@SuppressWarnings("exports")
	static final MediaType DEFAULT_MEDIA_TYPE = JStachioHttpMessageConverter.DEFAULT_MEDIA_TYPE;

	/**
	 * The default buffer limit before bailing on trying to set
	 * <code>Content-Length</code>. The default is
	 * "{@value JStachioHttpMessageConverter#DEFAULT_BUFFER_LIMIT}".
	 */
	static final int DEFAULT_BUFFER_LIMIT = JStachioHttpMessageConverter.DEFAULT_BUFFER_LIMIT;

	@SuppressWarnings("exports")
	@Override
	default void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String contentType = getContentType();
		response.setContentType(contentType);
		Charset charset = getMediaType().getCharset();
		if (charset == null) {
			charset = StandardCharsets.UTF_8;
		}
		try (var o = createOutput(charset, response)) {
			var context = createContext(model, request, response);
			jstachio().write(model(), context, o);
		}
	}

	/*
	 * If you want this public file an issue.
	 */
	private ContextNode createContext(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
		return ContextNode.of(model::get);
	}

	/**
	 * Creates the output from the servlet response to use for rendering.
	 * @param charset charset resolved from {@link #getMediaType()}
	 * @param response servlet response
	 * @return output that should be closed when finished
	 */
	@SuppressWarnings("exports")
	default CloseableEncodedOutput<IOException> createOutput(Charset charset, HttpServletResponse response) {
		return new ServletThresholdEncodedOutput(charset, response, DEFAULT_BUFFER_LIMIT);
	}

	/**
	 * Returns the jstachio singleton by default.
	 * @return stachio singleton by default.
	 * @see JStachio#setStatic(java.util.function.Supplier)
	 */
	default ContextJStachio jstachio() {
		return ContextJStachio.of(JStachio.of());
	}

	/**
	 * The model to be rendered by {@link #jstachio()}.
	 * @return model defaulting to <code>this</code> instance.
	 */
	default Object model() {
		return this;
	}

	@Override
	default String getContentType() {
		return getMediaType().toString();
	}

	/**
	 * The media type for this view. The default is
	 * "<code>text/html; charset=UTF-8</code>".
	 * @return the media type
	 */
	@SuppressWarnings("exports")
	default MediaType getMediaType() {
		return DEFAULT_MEDIA_TYPE;
	}

	/**
	 * Creates a spring view from a model with content type:
	 * "<code>text/html; charset=UTF-8</code>".
	 * @param model an instance of a class annotated with {@link JStache}.
	 * @return view ready for rendering
	 */
	public static JStachioModelView of(Object model) {
		return of(model, MediaType.TEXT_HTML.toString());
	}

	/**
	 * Creates a spring view from a model.
	 * @param model an instance of a class annotated with {@link JStache}.
	 * @param contentType See {@link #getContentType()}
	 * @return view ready for rendering
	 */
	public static JStachioModelView of(Object model, String contentType) {
		MediaType mediaType = MediaType.parseMediaType(contentType);
		return JStachioModelView.of(model, mediaType);
	}

	/**
	 * Creates a spring view from a model.
	 * @param model an instance of a class annotated with {@link JStache}.
	 * @param mediaType the mediaType
	 * @return view ready for rendering
	 */
	static JStachioModelView of(Object model, @SuppressWarnings("exports") MediaType mediaType) {
		return of(model, mediaType, JStachio.of());
	}

	/**
	 * Creates a spring view from a model.
	 * @param model an instance of a class annotated with {@link JStache}.
	 * @param mediaType the mediaType
	 * @param jstachio jstachio to use.
	 * @return view ready for rendering
	 */
	static JStachioModelView of(Object model, @SuppressWarnings("exports") MediaType mediaType, JStachio jstachio) {
		ContextJStachio contextJStachio = ContextJStachio.of(jstachio);
		/*
		 * TODO potentially make this public on the next minor version release.
		 */
		return new JStachioModelView() {
			@Override
			public Object model() {
				return model;
			}

			@Override
			public MediaType getMediaType() {
				return mediaType;
			}

			@Override
			public ContextJStachio jstachio() {
				return contextJStachio;
			}
		};
	}

}
