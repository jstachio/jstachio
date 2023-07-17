package io.jstach.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class CodeToJavadoc {

	public static void main(String[] args) {
		try {
			_main(args);
		} catch (IOException e) {
			e.printStackTrace(); //NOSONAR
			System.exit(1);
		}
	}
		
	static void _main(String[] args) throws IOException {
		var in = System.in;
		StringBuilder result = new StringBuilder();
		try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(in))) {
			String original;
			while ((original = reader.readLine()) != null) {
				String line = original;
				line = line.replace("\t", "  ") //
						.replace("<", "&lt;") //
						.replace(">", "&gt;") //
						.replace("@", "&#64;") //
						.replace("//", "&#47;&#47;");
				line = " * " + line + "\n";
				result.append(line);
			}
		}
		System.out.print(result.toString());

	}

}
