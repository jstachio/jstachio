package io.jstach.opt.spring.webmvc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.output.CloseableEncodedOutput;
import io.jstach.jstachio.output.ThresholdEncodedOutput;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Another way to use JStachio with Spring MVC is to have models implement Springs
 * {@link View} interface. You can enforce that your models implement this interface with
 * {@link JStacheInterfaces}.
 * <p>
 * The model will use the static jstachio singleton that will be the spring one.
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
	static final MediaType DEFAULT_MEDIA_TYPE = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);

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
		try (var o = new ServletThresholdEncodedOutput(charset, response)) {
			jstachio().write(model(), o);
		}
	}

	/**
	 * Creates the output from the servlet response to use for rendering.
	 * @param charset charset resolved from {@link #getMediaType()}
	 * @param response servlet response
	 * @return output that should be closed when finished
	 */
	@SuppressWarnings("exports")
	default CloseableEncodedOutput<IOException> createOutput(Charset charset, HttpServletResponse response) {
		return new ServletThresholdEncodedOutput(charset, response);
	}

	/**
	 * Returns the jstachio singleton by default.
	 * @return stachio singleton by default.
	 * @see JStachio#setStatic(java.util.function.Supplier)
	 */
	default JStachio jstachio() {
		return JStachio.of();
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
		};
	}

}

class ServletThresholdEncodedOutput extends ThresholdEncodedOutput<OutputStream, IOException> {

	private final HttpServletResponse response;

	public ServletThresholdEncodedOutput(Charset charset, HttpServletResponse response) {
		super(charset, calculateLimit(response));
		this.response = response;
	}

	private static int calculateLimit(HttpServletResponse response) {
		int limit = response.getBufferSize();
		if (limit <= 0) {
			/*
			 * It is probably lying here or its a unit test.
			 */
			return 1024 * 32;
		}
		return limit;
	}

	@Override
	protected void write(OutputStream consumer, byte[] bytes) throws IOException {
		consumer.write(bytes);
	}

	@Override
	protected OutputStream createConsumer(int size) throws IOException {
		if (size > -1) {
			response.setContentLength(size);
			/*
			 * It is already all in memory so we do not need a buffer.
			 */
			response.setBufferSize(0);
		}
		return response.getOutputStream();
	}

	@Override
	protected void close(OutputStream consumer) throws IOException {
		consumer.close();
	}

}
