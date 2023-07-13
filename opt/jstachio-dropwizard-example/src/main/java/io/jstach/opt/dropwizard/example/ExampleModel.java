package io.jstach.opt.dropwizard.example;

import io.jstach.jstache.JStache;
import io.jstach.opt.dropwizard.JStacheViewSupport;

/**
 * A JStache model using external template:
 * <code>src/main/resources/io/jstach/opt/dropwizard/example/ExampleModel.mustache</code>.
 * <p>
 * The static jstachio configuration is pulled from the
 * {@linkplain io.jstach.opt.dropwizard.example package-info}. Take note how the model
 * implements {@link JStacheViewSupport} and the static configuration will enforce this on
 * all annotated models.
 * @param message the message to display
 * @author agentgt
 * @see io.jstach.opt.dropwizard.example
 */
@JStache
public record ExampleModel(String message) implements JStacheViewSupport {

}
