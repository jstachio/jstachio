package io.jstach.opt.spring.webflux;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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
public interface JStachioModelView extends View {

	@Override
	default Mono<Void> render(Map<String, ?> model, MediaType contentType, ServerWebExchange exchange) {
		if (contentType != null) {
			exchange.getResponse().getHeaders().setContentType(contentType);
		}
		if (contentType != null) {
			exchange.getResponse().getHeaders().setContentType(contentType);
		}

		View view = new AbstractView() {

			@Override
			protected Mono<Void> renderInternal(Map<String, Object> model, MediaType contentType,
					ServerWebExchange exchange) {
				return exchange.getResponse().writeWith(Mono.fromCallable(() -> {
					try {
						byte[] bytes = String.valueOf(jstachio().execute(model())).getBytes(StandardCharsets.UTF_8);
						// just wrapping, no allocation
						return exchange.getResponse().bufferFactory().wrap(bytes);
					}
					catch (Exception ex) {
						throw new IllegalStateException("Failed to render script template", ex);
					}
				}));
			}

		};

		return view.render(model, contentType, exchange);
	}

	@Override
	default List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.TEXT_HTML);
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
	 * Creates a spring view from a model
	 * @param model an instance of a class annotated with {@link JStache}.
	 * @return view ready for rendering
	 */
	public static JStachioModelView of(Object model) {
		return new JStachioModelView() {
			@Override
			public Object model() {
				return model;
			}
		};
	}

}
