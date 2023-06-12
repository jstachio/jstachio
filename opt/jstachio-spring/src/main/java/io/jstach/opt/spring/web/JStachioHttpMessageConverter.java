package io.jstach.opt.spring.web;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ResponseBody;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.output.ByteBufferedOutputStream;

/**
 * Typesafe way to use JStachio in Spring Web.
 * <p>
 * For this to work the controllers need to return JStache models and have the controller
 * method return annotated with {@link ResponseBody}.
 * <p>
 * <strong>Example:</strong> <pre><code class="language-java">
 * &#64;JStache
 * public record HelloModel(String message){}
 *
 * &#64;GetMapping(value = "/")
 * &#64;ResponseBody
 * public HelloModel hello() {
 *     return new HelloModel("Spring Boot is now JStachioed!");
 * }
 * </code> </pre>
 *
 * @author agentgt
 *
 */
public class JStachioHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

	/**
	 * The default media type is "<code>text/html; charset=UTF-8</code>".
	 */
	static final MediaType DEFAULT_MEDIA_TYPE = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);

	private final JStachio jstachio;

	/**
	 * Create http converter from jstachio
	 * @param jstachio an instance usually created by spring
	 */
	public JStachioHttpMessageConverter(JStachio jstachio) {
		super(StandardCharsets.UTF_8, MediaType.TEXT_HTML, MediaType.ALL);
		this.jstachio = jstachio;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return jstachio.supportsType(clazz);
	}

	@Override
	public boolean canRead(Class<?> clazz, @SuppressWarnings("exports") MediaType mediaType) {
		return jstachio.supportsType(clazz);
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		throw new HttpMessageNotReadableException("Input not supported by JStachio", inputMessage);

	}

	@Override
	protected void writeInternal(Object t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		/*
		 * If we just write directly to the body we will get Transfer-Encoding: chunked
		 * which is almost never desired for HTML.
		 *
		 * TODO we should explore making this configurable or other options as this
		 * requires copying all the data.
		 */
		try (ByteBufferedOutputStream buffer = new ByteBufferedOutputStream()) {
			// The try - with is not necessary but keeps linters happy
			jstachio.write(t, Output.of(buffer, charset()));
			int size = buffer.size();
			var headers = outputMessage.getHeaders();
			headers.setContentLength(size);
			/*
			 * We have to override the content type here because if we do not Spring
			 * appears to default to application/json if the Accept does not include HTML.
			 */
			headers.setContentType(DEFAULT_MEDIA_TYPE);
			/*
			 * buffer.toByteArray copies which we do not want so we use toBuffer which
			 * does not
			 */
			var bytes = buffer.toBuffer().array();
			var body = outputMessage.getBody();
			body.write(bytes, 0, size);
		}
	}

	protected Charset charset() {
		return Objects.requireNonNull(getDefaultCharset());
	}

}
