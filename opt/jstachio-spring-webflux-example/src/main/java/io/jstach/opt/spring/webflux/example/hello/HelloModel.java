package io.jstach.opt.spring.webflux.example.hello;

import io.jstach.jstache.JStache;

/**
 * Model using a resource template that is in src/main/resources/views. The path will be
 * expanded via the {@link io.jstach.jstache.JStacheConfig} on the projects module.
 * 
 * @author agentgt
 * @param message The greeting message
 */
@JStache(path = "hello")
public record HelloModel(String message) {

}
