package io.jstach.apt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public interface FormatterTypes {

	public boolean isMatch(String className);

	public static FormatterTypes acceptOnlyKnownTypes() {
		return new FormatterTypes() {

			@Override
			public boolean isMatch(String className) {
				return false;
			}
		};
	}

	class ConfiguredFormatterTypes implements FormatterTypes {

		private final List<String> classNames;

		private final List<Pattern> patterns;

		public ConfiguredFormatterTypes(List<String> classNames, List<String> classPatterns) {
			super();
			this.classNames = classNames;
			List<Pattern> patterns = new ArrayList<>();
			for (String p : classPatterns) {
				patterns.add(Pattern.compile(p));
			}
			this.patterns = List.copyOf(patterns);
		}

		public boolean isMatch(String className) {
			if (classNames.isEmpty() && patterns.isEmpty()) {
				return true;
			}
			for (String n : classNames) {
				if (n.equals(className)) {
					return true;
				}
			}
			for (var p : patterns) {
				if (p.matcher(className).matches()) {
					return true;
				}
			}
			return false;
		}

	}

}
