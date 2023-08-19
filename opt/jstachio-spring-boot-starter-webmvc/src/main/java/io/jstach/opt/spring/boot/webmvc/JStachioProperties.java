package io.jstach.opt.spring.boot.webmvc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;

import io.jstach.opt.spring.web.JStachioHttpMessageConverter;

/**
 * {@link ConfigurationProperties @ConfigurationProperties} for JStachio.
 *
 * @author agentgt
 *
 */
@ConfigurationProperties(prefix = "spring.jstachio.webmvc")
public class JStachioProperties {

	private int bufferLimit = JStachioHttpMessageConverter.DEFAULT_BUFFER_LIMIT;

	private MediaType mediaType = JStachioHttpMessageConverter.DEFAULT_MEDIA_TYPE;

	/**
	 * Do nothing constructor for Spring
	 */
	public JStachioProperties() {
	}

	/**
	 * Because JStachio does pre-encoding of templates it can handle buffering the
	 * template output better than the builtin servlet framework buffering while reliably
	 * setting the <code>Content-Length</code>. If the servlet frameworks buffer is set
	 * higher than this value it will be used as the limit instead.
	 * @return the buffer limit in number of bytes which by default is
	 * "{@value JStachioHttpMessageConverter#DEFAULT_BUFFER_LIMIT}".
	 */
	public int getBufferLimit() {
		return bufferLimit;
	}

	/**
	 * See {@link #getBufferLimit()}
	 * @param bufferLimit a zero or negative number will disable buffering.
	 */
	public void setBufferLimit(int bufferLimit) {
		this.bufferLimit = bufferLimit;
	}

	/**
	 * The media type which by default is "<code>text/html; charset=UTF-8</code>". If the
	 * charset is not in the media type than UTF-8 will be used.
	 * @return media type ideally with charset.
	 */
	@SuppressWarnings("exports")
	public MediaType getMediaType() {
		return mediaType;
	}

	/**
	 * See {@link #getMediaType()}
	 * @param mediaType media type ideally with charset.
	 */
	public void setMediaType(@SuppressWarnings("exports") MediaType mediaType) {
		this.mediaType = mediaType;
	}

}
