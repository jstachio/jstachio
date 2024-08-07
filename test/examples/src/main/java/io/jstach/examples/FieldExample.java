package io.jstach.examples;

import io.jstach.jstache.JStache;

public class FieldExample {

	/*
	 * https://github.com/jstachio/jstachio/issues/397
	 */
	@JStache(template = """
			{{fieldA}}
			{{fieldB}}
			{{modelC.fieldC}}
			""")
	public class FieldExampleModelA extends FieldExampleModelB {

		/*
		 * Package friendly is allowed if it is directly on the class annotated.
		 */
		String fieldA;

	}

	public class FieldExampleModelB {

		public String fieldB;

		public FieldExampleModelC modelC;

	}

	public class FieldExampleModelC {

		public String fieldC;

	}

}
