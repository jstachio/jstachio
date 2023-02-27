package io.jstach.examples.using;

import io.jstach.examples.using.MyConfig.MyConfigInterface;
import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstache.JStacheName;
import io.jstach.jstache.JStachePath;

@JStachePath(suffix = ".config")
@JStacheConfig(naming = @JStacheName(suffix = "Template"),
		interfacing = @JStacheInterfaces(templateImplements = MyConfigInterface.class))
public enum MyConfig {

	;
	public interface MyConfigInterface {

	}

}
