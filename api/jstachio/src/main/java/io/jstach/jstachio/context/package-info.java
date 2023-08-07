/**
 * JStachio extended Mustache context support for context stack as well as Map and JSON
 * tree like models.
 * <p>
 * Models traditionally in web applications and template engines in general are Map based.
 * JStachio prefers stronger typing than that however there are many scenarios where Map
 * approach might be better for legacy reasons or because of variables that need to be
 * added after the model has been created. An example is web applications where request
 * based attributes may need to be available to the model such as CSRF token.
 * <p>
 * {@linkplain io.jstach.jstachio.context.ContextJStachio A special JStachio} allows you
 * to pass in a {@linkplain io.jstach.jstachio.context.ContextNode context} that will be
 * bound with {@value io.jstach.jstachio.context.ContextNode#CONTEXT_BINDING_NAME} as the
 * variable name.
 * <p>
 * On the otherhand if the desire is to just render Map as a root object and are willing
 * to accept the lack of type checking one can make an annotated
 * {@link io.jstach.jstache.JStache} model that extends
 * {@link io.jstach.jstachio.context.ObjectContext}.
 * <p>
 * <em>None of this behavior in this package is available to generated code that is
 * {@link io.jstach.jstache.JStacheType#STACHE}.</em> By default
 * {@link io.jstach.jstache.JStacheType#JSTACHIO} templates are context aware. To disable
 * use {@link io.jstach.jstache.JStacheFlags.Flag#CONTEXT_SUPPORT_DISABLE}.
 *
 * @apiNote Much of this package is an <strong>experimental</strong> extension of JStachio
 * and while we try to limit public API additions the proper use cases and design are
 * still ongoing.
 */
@org.eclipse.jdt.annotation.NonNullByDefault
package io.jstach.jstachio.context;