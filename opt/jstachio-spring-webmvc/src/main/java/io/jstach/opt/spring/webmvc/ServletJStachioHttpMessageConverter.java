package io.jstach.opt.spring.webmvc;

import java.io.IOException;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.output.CloseableEncodedOutput;
import io.jstach.opt.spring.web.JStachioHttpMessageConverter;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A servlet server aware {@link JStachioHttpMessageConverter} that will leverage
 * {@link HttpServletResponse#setBufferSize(int)} to reduce duplicate buffering.
 *
 * @author agentgt
 *
 */
public class ServletJStachioHttpMessageConverter extends JStachioHttpMessageConverter {

	/**
	 * See {@link JStachioHttpMessageConverter}.
	 * @param jstachio not null.
	 */
	public ServletJStachioHttpMessageConverter(JStachio jstachio) {
		this(jstachio, DEFAULT_MEDIA_TYPE, DEFAULT_BUFFER_LIMIT);
	}

	/**
	 * See {@link JStachioHttpMessageConverter}
	 * @param jstachio not null.
	 * @param mediaType used for setContentType
	 * @param bufferLimit limit used if the response is not a
	 * {@link ServletServerHttpResponse}.
	 */
	protected ServletJStachioHttpMessageConverter(JStachio jstachio, MediaType mediaType, int bufferLimit) {
		super(jstachio, mediaType, bufferLimit);
	}

	@Override
	protected CloseableEncodedOutput<IOException> createOutput(HttpOutputMessage message) {
		if (message instanceof ServletServerHttpResponse sr) {
			return new ServletThresholdEncodedOutput(getDefaultCharset(), sr.getServletResponse());
		}
		return super.createOutput(message);
	}

}
