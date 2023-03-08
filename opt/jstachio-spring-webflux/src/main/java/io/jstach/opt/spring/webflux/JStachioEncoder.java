package io.jstach.opt.spring.webflux;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

import io.jstach.jstachio.JStachio;
import reactor.core.publisher.Flux;

/**
 * Encodes a JStachio model into a bytes to be used as output from a webflux reactive
 * controller.
 *
 * @author agentgt
 * @author dsyer
 */
public class JStachioEncoder extends AbstractSingleValueEncoder<Object> {

	private final JStachio jstachio;

	/**
	 * Create the encoder from a JStachio
	 * @param jstachio not <code>null</code>.
	 */
	public JStachioEncoder(JStachio jstachio) {
		super(MediaType.TEXT_HTML);
		this.jstachio = jstachio;
	}

	@Override
	public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
		Class<?> clazz = elementType.toClass();
		return clazz != Object.class && !CharSequence.class.isAssignableFrom(clazz) && jstachio.supportsType(clazz);
	}

	@Override
	protected Flux<DataBuffer> encode(Object event, DataBufferFactory bufferFactory, ResolvableType type,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		return Flux.just(encodeValue(event, bufferFactory, type, mimeType, hints));
	}

	@Override
	public DataBuffer encodeValue(Object event, DataBufferFactory bufferFactory, ResolvableType valueType,
			MimeType mimeType, Map<String, Object> hints) {
		if (logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
			String logPrefix = Hints.getLogPrefix(hints);
			logger.debug(logPrefix + "Writing [" + event + "]");
		}

		Charset charset = StandardCharsets.UTF_8;
		return bufferFactory.wrap(jstachio.execute(event).getBytes(charset));
	}

}