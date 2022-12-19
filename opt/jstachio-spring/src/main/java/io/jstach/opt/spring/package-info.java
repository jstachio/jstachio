/**
 * <h2>JStachio Spring Core Integration</h2>
 *
 * <h3>Config and template finding integration</h3>
 *
 * {@link io.jstach.opt.spring.SpringJStachioExtension} will use Spring Environment
 * abstraction for config and template finding. You may wire in other extensions such as
 * JMustache as well instead of relying on the ServiceLoader.
 */
@org.eclipse.jdt.annotation.NonNullByDefault
package io.jstach.opt.spring;