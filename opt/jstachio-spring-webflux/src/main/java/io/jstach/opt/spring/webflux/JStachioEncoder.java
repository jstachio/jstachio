package io.jstach.opt.spring.webflux;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.server.NotAcceptableStatusException;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import reactor.core.publisher.Flux;

/**
 * Encodes a JStachio model into a bytes to be used as output from a webflux reactive
 * controller.
 *
 * @author agentgt
 * @author dsyer
 */
@SuppressWarnings("exports")
public class JStachioEncoder extends AbstractSingleValueEncoder<Object> {

	private final JStachio jstachio;

	private final int allocateBufferSize;

	private final MediaType mediaType;

	/**
	 * TODO make public on minor release
	 */
	static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

	/**
	 * The default media type is "<code>text/html; charset=UTF-8</code>".
	 */
	static final MediaType DEFAULT_MEDIA_TYPE = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);

	/**
	 * Create the encoder from a JStachio
	 * @param jstachio not <code>null</code>.
	 */
	public JStachioEncoder(JStachio jstachio) {
		this(jstachio, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Create the encoder from a JStachio
	 * @param jstachio not <code>null</code>.
	 * @param allocateBufferSize how much to initially allocate from the buffer factory
	 */
	public JStachioEncoder(JStachio jstachio, int allocateBufferSize) {
		this(jstachio, allocateBufferSize, DEFAULT_MEDIA_TYPE);
	}

	/*
	 * TODO possibly make public on minor release
	 */
	JStachioEncoder(JStachio jstachio, int allocateBufferSize, MediaType mediaType) {
		super(mediaType);
		this.jstachio = jstachio;
		this.allocateBufferSize = allocateBufferSize;
		this.mediaType = mediaType;
	}

	@Override
	public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
		Class<?> clazz = elementType.toClass();
		/*
		 * WE MUST BLOCK all other encoders regardless of mimetype if it is a jstache
		 * model otherwise we could serialize a model that has sensitive data as JSON.
		 */
		return clazz != Object.class && !CharSequence.class.isAssignableFrom(clazz) && jstachio.supportsType(clazz);
	}

	@Override
	protected Flux<DataBuffer> encode(Object event, DataBufferFactory bufferFactory, ResolvableType type,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		return Flux.just(encodeValue(event, bufferFactory, type, mimeType, hints));
	}

	@Override
	public DataBuffer encodeValue(Object event, DataBufferFactory bufferFactory, ResolvableType valueType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		if (logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
			String logPrefix = Hints.getLogPrefix(hints);
			logger.debug(logPrefix + "Writing [" + event + "]");
		}

		/*
		 * We check the media type to see if it matches otherwise Spring will default to
		 * application/json and will send Content-Type application/json back but return
		 * our almost always NOT JSON body (jstachio should obviously not be used for
		 * generating json).
		 *
		 * See #176
		 */
		if (mimeType != null && !mimeType.isCompatibleWith(this.mediaType)) {
			/*
			 * Returning 406 is not ideal for HTML responses
			 *
			 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/406
			 *
			 * > In practice, this error is very rarely used. Instead of responding using
			 * this > error code, which would be cryptic for the end user and difficult to
			 * fix, > servers ignore the relevant header and serve an actual page to the
			 * user. It > is assumed that even if the user won't be completely happy, they
			 * will prefer > this to an error code.
			 */
			throw new NotAcceptableStatusException(List.of(this.mediaType));
		}

		return encode(jstachio, event, bufferFactory, allocateBufferSize, this.mediaType.getCharset());

	}

	static DataBuffer encode( //
			JStachio jstachio, //
			Object event, //
			DataBufferFactory bufferFactory, //
			int bufferSize, //
			@Nullable Charset charset) {
		Template<Object> template;
		try {
			template = jstachio.findTemplate(event);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (charset == null) {
			charset = template.templateCharset();
		}

		DataBufferOutput output = new DataBufferOutput(bufferFactory.allocateBuffer(bufferSize), charset);

		return template.write(event, output).getBuffer();
	}

}