package io.jstach.examples.jstacheifaces;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheInterfaces;

@JStacheInterfaces(templateExtends = AbstractSome.class)
@JStache(template = "asdfasdf")
public record SomeModel(String message) {

}
