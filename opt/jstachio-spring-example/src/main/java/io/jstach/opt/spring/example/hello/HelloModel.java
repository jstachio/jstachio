package io.jstach.opt.spring.example.hello;

import io.jstach.jstache.JStache;
import io.jstach.opt.spring.webmvc.JStachioModelView;

/**
 * Model using a resource template that is in src/main/resources/views. The path will be
 * expanded via the {@link io.jstach.jstache.JStacheConfig} on the projects module.
 * 
 * @author agentgt
 */
@JStache(path = "hello")
public record HelloModel(String message) implements JStachioModelView {

}
