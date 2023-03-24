/**
 * JStachio Core Runtime API.
 *
 * The interfaces and classes in this package are used by generated code that is of type
 * {@link io.jstach.jstache.JStacheType#JSTACHIO}.
 * <p>
 * The notable interfaces this package provides are:
 * <ul>
 * <li>{@link io.jstach.jstachio.Appender}</li>
 * <li>{@link io.jstach.jstachio.Escaper}</li>
 * <li>{@link io.jstach.jstachio.Formatter}</li>
 * </ul>
 *
 * When a template outputs an <strong>escaped</strong> variable the callstack is as
 * follows:
 *
 * <pre>
 * formatter --&gt; escaper --&gt; appendable
 * </pre>
 *
 * When a template outputs an <strong>unescaped</strong> variable the callstack is as
 * follows:
 *
 * <pre>
 * formatter --&gt; appender --&gt; appendable
 * </pre>
 *
 * When a template outputs anything else (e.g. HTML markup) it writes directly to the
 * appendable.
 *
 * @see io.jstach.jstachio.spi
 * @see io.jstach.jstache
 */
@org.eclipse.jdt.annotation.NonNullByDefault
package io.jstach.jstachio;
