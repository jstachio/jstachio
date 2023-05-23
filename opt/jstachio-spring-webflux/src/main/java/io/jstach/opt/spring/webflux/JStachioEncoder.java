package io.jstach.opt.spring.webflux;

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
import io.jstach.jstachio.Template;
import io.jstach.jstachio.Template.EncodedTemplate;
import io.jstach.jstachio.output.ByteBufferedOutputStream;
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

	private final int allocateBufferSize;

	/**
	 * Create the encoder from a JStachio
	 * @param jstachio not <code>null</code>.
	 */
	public JStachioEncoder(JStachio jstachio) {
		this(jstachio, 4 * 1024);
	}

	/**
	 * Create the encoder from a JStachio
	 * @param jstachio not <code>null</code>.
	 * @param allocateBufferSize how much to initially allocate from the buffer factory
	 */
	public JStachioEncoder(JStachio jstachio, int allocateBufferSize) {
		super(MediaType.TEXT_HTML);
		this.jstachio = jstachio;
		this.allocateBufferSize = allocateBufferSize;
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

		Template<Object> template;
		try {
			template = jstachio.findTemplate(event);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		DataBufferOutput output = new DataBufferOutput(bufferFactory.allocateBuffer(allocateBufferSize),
				template.templateCharset());

		return template.write(event, output).getBuffer();

	}

}