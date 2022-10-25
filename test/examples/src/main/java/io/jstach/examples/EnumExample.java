package io.jstach.examples;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStacheFlags;
import io.jstach.annotation.JStacheFlags.Flag;

@JStache(template = """
		{{message}}{{#sign.RED_LIGHT}}
		STOP!!!!{{/sign.RED_LIGHT}}
		{{^sign.RED_LIGHT}}Go {{desc}}!{{/sign.RED_LIGHT}}""")
@JStacheFlags(flags = Flag.DEBUG)
public record EnumExample(String message, Sign sign) {
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
