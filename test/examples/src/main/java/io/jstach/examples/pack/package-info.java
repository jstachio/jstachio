@io.jstach.jstache.JStachePath(prefix = "io/jstach/examples2/", suffix = ".mustache")
@io.jstach.jstache.JStacheInterfaces( //
		templateImplements = { io.jstach.examples.pack.BlahInf.class },
		modelImplements = { io.jstach.examples.pack.BlahInf.class })
@io.jstach.jstache.JStacheFormatterTypes(types = { io.jstach.examples.pack.SomeUnknownType.class })
@io.jstach.jstache.JStacheConfig(formatter = io.jstach.jstachio.formatters.SpecFormatter.class, //
		type = io.jstach.jstache.JStacheType.STACHE)
package io.jstach.examples.pack;
