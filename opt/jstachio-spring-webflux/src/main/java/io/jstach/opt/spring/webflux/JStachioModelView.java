package io.jstach.opt.spring.webflux;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.result.view.AbstractView;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.server.ServerWebExchange;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.JStachio;
import reactor.core.publisher.Mono;

/**
 * One way to use JStachio with Spring Webflux is to use this special View that will
 * delegate to JStachio to render.
 * <p>
 * By default this view interface will use the static jstachio singleton which normally is
 * the correct Spring wired version if configured correctly.
 *
 * @author agentgt
 * @author dsyer
 *
 */
@SuppressWarnings("exports")
public interface JStachioModelView extends View {

	@Override
	default Mono<Void> render( //
			@Nullable Map<String, ?> model, //
			@Nullable MediaType contentType, //
			ServerWebExchange exchange) {
		if (contentType != null) {
			exchange.getResponse().getHeaders().setContentType(contentType);
		}
		if (contentType != null) {
			exchange.getResponse().getHeaders().setContentType(contentType);
		}

		View view = new AbstractView() {

			@Override
			protected Mono<Void> renderInternal(Map<String, Object> model, @Nullable MediaType contentType,
					ServerWebExchange exchange) {
				var response = exchange.getResponse();
				return response.writeWith(Mono.fromCallable(() -> {
					var bufferFactory = response.bufferFactory();
					var mediaType = mediaType();
					DataBuffer buffer = JStachioEncoder.encode(jstachio(), model(), bufferFactory, bufferSize(),
							mediaType.getCharset());
					var headers = response.getHeaders();
					headers.setContentType(mediaType);
					/*
					 * For some reason the webflux WebTestClient gets content-length of -1
					 * during unit test but when actually deployed the pipeline will
					 * implicitly set the content length.
					 *
					 * So we just go ahead and explicitely set hoping that does not hurt
					 * anything.
					 */
					int length = buffer.readableByteCount();
					headers.setContentLength(length);
					return buffer;
				}));
			}

		};

		return view.render(model, contentType, exchange);
	}

	@Override
	default List<MediaType> getSupportedMediaTypes() {
		return List.of(mediaType());
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

	/**
	 * The initial size of the buffer allocated to be used for rendering.
	 * @return buffer size the default is 4K.
	 */
	default int bufferSize() {
		return JStachioEncoder.DEFAULT_BUFFER_SIZE;
	}

	/**
	 * The default media type for the view.
	 * @return media type the default is "<code>text/html; charset=UTF-8</code>"
	 */
	default MediaType mediaType() {
		return JStachioEncoder.DEFAULT_MEDIA_TYPE;
	}

	/**
	 * Creates a spring view from a model
	 * @param model an instance of a class annotated with {@link JStache}.
	 * @return view ready for rendering
	 */
	public static JStachioModelView of(Object model) {
		/*
		 * TODO in theory we could resolve media type here by finding the template right
		 * away.
		 */
		return new JStachioModelView() {
			@Override
			public Object model() {
				return model;
			}
		};
	}

}
