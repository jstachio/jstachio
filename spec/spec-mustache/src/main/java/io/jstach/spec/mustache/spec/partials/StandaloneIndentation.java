package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "partials/StandaloneIndentation.mustache")
@JStachPartialMapping({
@JStachPartial(name="partial", template="|\n{{{content}}}\n|\n"),
})
public class StandaloneIndentation extends SpecModel {
}
