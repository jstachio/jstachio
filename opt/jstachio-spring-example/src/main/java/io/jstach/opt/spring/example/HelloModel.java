package io.jstach.opt.spring.example;

import io.jstach.jstache.JStache;

/**
 * @hidden
 * @author agentgt
 */
@JStache(path = "hello")
public record HelloModel(String message) {

}