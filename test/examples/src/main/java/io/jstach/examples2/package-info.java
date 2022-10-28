@io.jstach.annotation.JStachePath(prefix = "io/jstach/examples2/", suffix = ".mustache")
@io.jstach.annotation.JStacheInterfaces( //
		rendererImplements = { io.jstach.examples2.BlahInf.class },
		modelImplements = { io.jstach.examples2.BlahInf.class })
@io.jstach.annotation.JStacheFormatterTypes(types = { io.jstach.examples2.SomeUnknownType.class },
		formatter = io.jstach.formatters.SpecFormatter.class)

package io.jstach.examples2;

import io.jstach.formatters.SpecFormatter;
