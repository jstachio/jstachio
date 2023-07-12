/**
 * <h2>Spring Web MVC integration</h2>
 *
 * {@link io.jstach.opt.spring.webmvc.JStachioModelView} is a
 * {@link org.springframework.web.servlet.View} implementation that can be returned
 * directly from {@link org.springframework.stereotype.Controller} methods without needing
 * the {@link org.springframework.web.bind.annotation.ResponseBody} annotation.
 * <p>
 * This module also provides
 * {@link io.jstach.opt.spring.webmvc.ServletJStachioHttpMessageConverter} which is a
 * message converter optimized for servlet environments.
 * <p>
 * This integration is tied to the servlet API and thus will need it as a dependency.
 */
@org.eclipse.jdt.annotation.NonNullByDefault
package io.jstach.opt.spring.webmvc;