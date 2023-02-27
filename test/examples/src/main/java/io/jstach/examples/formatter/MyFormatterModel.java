package io.jstach.examples.formatter;

import java.time.LocalDate;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;

@JStacheConfig(formatter = MyFormatter.class)
@JStache(template = "{{date}}")
public record MyFormatterModel(LocalDate date) {

}
