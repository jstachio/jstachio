/**
 * JMustache extension to JStachio to enable dynamic development of templates.
 * <p>
 * This extension will use JMustache instead of JStachio for rendering. The idea of this
 * extension is to allow you to edit Mustache templates in real time without waiting for
 * the compile reload cycle.
 * <p>
 * If this extension is enabled which it is by default if the ServiceLoader finds it
 * JMustache will be used when a runtime filtered rendering call is made (see
 * {@link JStachio}).
 *
 */
@org.eclipse.jdt.annotation.NonNullByDefault
package io.jstach.jmustache;

import io.jstach.JStachio;
