@io.jstach.jstache.JStachePath(prefix = "io/jstach/examples2/", suffix = ".mustache")
@io.jstach.jstache.JStacheInterfaces( //
		templateImplements = { io.jstach.examples.pack.BlahInf.class },
		modelImplements = { io.jstach.examples.pack.BlahInf.class })
@io.jstach.jstache.JStacheFormatterTypes(types = { io.jstach.examples.pack.SomeUnknownType.class },
		formatter = io.jstach.jstachio.formatters.SpecFormatter.class)

package io.jstach.examples.pack;

import io.jstach.jstachio.formatters.SpecFormatter;
