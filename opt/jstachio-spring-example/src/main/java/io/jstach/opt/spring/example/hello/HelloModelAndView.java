package io.jstach.opt.spring.example.hello;

import org.springframework.web.servlet.View;

import io.jstach.jstache.JStache;
import io.jstach.opt.spring.webmvc.JStachioModelView;

/**
 * Model that implements {@link JStachioModelView} using a resource template that is in
 * src/main/resources/views. The path will be expanded via the
 * {@link io.jstach.jstache.JStacheConfig} on the projects module.
 * <p>
 * Because this model implements a Spring {@link View} you can return it directly from a
 * controller.
 * @author agentgt
 * @param message The greeting message
 */
@JStache(path = "hello")
public record HelloModelAndView(String message) implements JStachioModelView {

}
