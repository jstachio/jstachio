package io.jstach.test.stache;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheType;

/**
 * Simple class to test zero code generation.
 */
@JStacheConfig(type = JStacheType.STACHE)
@JStache(path = "test.mustache")
public record Issue86Model() {

}
