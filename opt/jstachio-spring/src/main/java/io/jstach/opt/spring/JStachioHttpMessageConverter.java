package io.jstach.opt.spring;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

	private final JStachio jstachio;

	/**
	 * Create http converter from jstachio
	 * @param jstachio an instance usually created by spring
	 */
	public JStachioHttpMessageConverter(JStachio jstachio) {
		super(StandardCharsets.UTF_8, MediaType.TEXT_HTML);
		this.jstachio = jstachio;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return jstachio.supportsType(clazz);
	}

	@Override
	public boolean canRead(Class<?> clazz, @SuppressWarnings("exports") MediaType mediaType) {
		return false;
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		throw new HttpMessageNotReadableException("Input not supported by JStachio", inputMessage);

	}

	@Override
	protected void writeInternal(Object t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Charset charset = getDefaultCharset();
		if (charset == null) {
			charset = StandardCharsets.UTF_8;
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(outputMessage.getBody(), charset))) {
			jstachio.execute(t, writer);
		}
	}

}
