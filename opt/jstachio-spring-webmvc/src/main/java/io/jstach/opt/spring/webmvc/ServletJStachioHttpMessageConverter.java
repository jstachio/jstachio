package io.jstach.opt.spring.webmvc;

import java.io.IOException;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output.CloseableEncodedOutput;
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
	 * See {@link JStachioHttpMessageConverter}
	 * @param jstachio not null.
	 * @param mediaType used for setContentType
	 * @param bufferLimit limit used if the response is not a
	 * {@link ServletServerHttpResponse}.
	 */
	public ServletJStachioHttpMessageConverter(JStachio jstachio, @SuppressWarnings("exports") MediaType mediaType,
			int bufferLimit) {
		super(jstachio, mediaType, bufferLimit);
	}

	@Override
	protected CloseableEncodedOutput<IOException> createOutput(HttpOutputMessage message) {
		if (message instanceof ServletServerHttpResponse sr) {
			return createOutput(sr.getServletResponse());
		}
		return super.createOutput(message);
	}

	/**
	 * Create the output from a servlet response.
	 * @param response servlet response
	 * @return closeable output.
	 */
	protected CloseableEncodedOutput<IOException> createOutput(HttpServletResponse response) {
		return new ServletThresholdEncodedOutput(getDefaultCharset(), response, bufferLimit);
	}

}
