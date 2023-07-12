package io.jstach.opt.spring.web;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ResponseBody;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output.CloseableEncodedOutput;
import io.jstach.jstachio.output.ByteBufferEncodedOutput;
import io.jstach.jstachio.output.ChunkEncodedOutput;
import io.jstach.jstachio.output.LimitEncodedOutput;
import io.jstach.jstachio.output.ThresholdEncodedOutput;

/**
 * Type-safe way to use JStachio in Spring Web.
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
 * </code> </pre> Because JStachio by default pre-encodes the static text parts of the
 * template the output strategy handles the buffering instead of the framework (usually
 * servlet) to improve performance and to reliable set <code>Content-Length</code>. This
 * can be changed by overriding {@link #createOutput(HttpOutputMessage)}.
 *
 * @author agentgt
 *
 */
public class JStachioHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

	/**
	 * The default media type is "<code>text/html; charset=UTF-8</code>".
	 */
	@SuppressWarnings("exports")
	public static final MediaType DEFAULT_MEDIA_TYPE = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);

	/**
	 * The default buffer limit before bailing on trying to set
	 * <code>Content-Length</code>. The default is "{@value #DEFAULT_BUFFER_LIMIT}".
	 */
	public static final int DEFAULT_BUFFER_LIMIT = 1024 * 64;

	private final JStachio jstachio;

	private final MediaType mediaType;

	/**
	 * The maximum amount of bytes to buffer.
	 */
	protected final int bufferLimit;

	/**
	 * Create http converter from jstachio
	 * @param jstachio an instance usually created by spring
	 */
	public JStachioHttpMessageConverter(JStachio jstachio) {
		this(jstachio, DEFAULT_MEDIA_TYPE, DEFAULT_BUFFER_LIMIT);
	}

	/**
	 * Creates a message converter with media type and buffer limit.
	 * @param jstachio an instance usually created by spring
	 * @param mediaType used to set ContentType
	 * @param bufferLimit buffer limit before bailing on trying to set
	 * <code>Content-Length</code>.
	 */
	protected JStachioHttpMessageConverter(JStachio jstachio, MediaType mediaType, int bufferLimit) {
		super(resolveCharset(mediaType), mediaType, MediaType.ALL);
		this.jstachio = jstachio;
		this.mediaType = mediaType;
		this.bufferLimit = bufferLimit;
	}

	private static Charset resolveCharset(MediaType mediaType) {
		var charset = mediaType.getCharset();
		if (charset == null) {
			return StandardCharsets.UTF_8;
		}
		return charset;
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
		 * We have to override the content type here because if we do not Spring appears
		 * to default to application/json if the Accept does not include HTML.
		 */
		var headers = outputMessage.getHeaders();

		/*
		 * If we just write directly to the body without resolving content length first it
		 * might not get set and we MIGHT get Transfer-Encoding: chunked which is almost
		 * never desired for HTML.
		 */
		headers.setContentType(mediaType);
		try (CloseableEncodedOutput<IOException> output = createOutput(outputMessage)) {
			jstachio.write(t, output);
		}
	}

	/**
	 * Create the buffered output to use when executing JStachio.
	 * @param message response.
	 * @return the output ready for writing to.
	 * @see ByteBufferEncodedOutput
	 * @see ChunkEncodedOutput
	 * @see LimitEncodedOutput
	 */
	protected CloseableEncodedOutput<IOException> createOutput(HttpOutputMessage message) {
		return new HttpOutputMessageEncodedOutput(getDefaultCharset(), message, bufferLimit);
	}

}

class HttpOutputMessageEncodedOutput extends ThresholdEncodedOutput.OutputStreamThresholdEncodedOutput {

	private final HttpOutputMessage response;

	public HttpOutputMessageEncodedOutput(Charset charset, HttpOutputMessage response, int limit) {
		super(charset, limit);
		this.response = response;
	}

	@Override
	protected OutputStream createConsumer(int size) throws IOException {
		if (size > -1) {
			response.getHeaders().setContentLength(size);
		}
		return response.getBody();
	}

}
