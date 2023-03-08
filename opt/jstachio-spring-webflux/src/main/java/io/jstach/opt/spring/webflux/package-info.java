/**
 * <h2>Spring Webflux integration</h2>
 *
 * This package provides a
 * {@link org.springframework.core.codec.AbstractSingleValueEncoder} that will encode
 * JStache models that are contained by Flux or Monos.
 * <p>
 * To add the encoder to your application use:
 * {@link org.springframework.web.reactive.config.WebFluxConfigurer}.
 * <p>
 * <em><strong>N.B.</strong> JStachio generated code is not reactive! However in practice
 * it matters little if your models do not contain reactive datatypes (which jstachio
 * currently does not support anyway) and are not generating massive responses. </em>
 * <p>
 * If you do need streaming or have a rather large result it might be best to use a Flux
 * of models and then apply reactive operators to add header and footer.
 */
@org.eclipse.jdt.annotation.NonNullByDefault
package io.jstach.opt.spring.webflux;