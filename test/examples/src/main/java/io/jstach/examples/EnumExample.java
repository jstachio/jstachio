package io.jstach.examples;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;

@JStache(template = """
		{{message}}{{#sign.RED_LIGHT}}
		STOP!!!!{{/sign.RED_LIGHT}}
		{{^sign.RED_LIGHT}}Go {{desc}}!{{/sign.RED_LIGHT}}""")
@JStacheFlags(flags = Flag.DEBUG)
record EnumExample(String message, Sign sign) {
	public enum Sign {

		RED_LIGHT("stop"), GREEN_LIGHT("go"), YELLOW_LIGHT("proceeed with caution");

		private final String desc;

		private Sign(String desc) {
			this.desc = desc;
		}

		public String desc() {
			return this.desc;
		}

	}
}
