/**
 * JStachio Spring Integration.
 *
 * <h2>Config and template finding integration</h2>
 *
 * {@link io.jstach.opt.spring.SpringJStachioExtension} will use Spring Environment
 * abstraction for config and template finding. You may wire in other extensions such as
 * JMustache as well instead of relying on the ServiceLoader.
 *
 * <h2>MVC integration</h2>
 *
 * Notably {@link io.jstach.opt.spring.JStachioHttpMessageConverter} provides a unique way
 * to do MVC in a typesafe way instead of the normal {@code Map<String,String>} model and
 * {@link java.lang.String} view way (ModelAndView).
 */
package io.jstach.opt.spring;