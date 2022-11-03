@io.jstach.annotation.JStachePath(prefix = "io/jstach/examples2/", suffix = ".mustache")
@io.jstach.annotation.JStacheInterfaces( //
		rendererImplements = { io.jstach.examples.pack.BlahInf.class },
		modelImplements = { io.jstach.examples.pack.BlahInf.class })
@io.jstach.annotation.JStacheFormatterTypes(types = { io.jstach.examples.pack.SomeUnknownType.class },
		formatter = io.jstach.formatters.SpecFormatter.class)

package io.jstach.examples.pack;

import io.jstach.formatters.SpecFormatter;
