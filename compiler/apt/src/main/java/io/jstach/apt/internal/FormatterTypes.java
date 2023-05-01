package io.jstach.apt.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.lang.model.type.DeclaredType;

import io.jstach.apt.internal.context.JavaLanguageModel;

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

		private final Set<String> classNames;

		private final Set<DeclaredType> ifaces;

		private final List<Pattern> patterns;

		public ConfiguredFormatterTypes(Collection<String> classNames, List<String> classPatterns) {
			super();
			this.classNames = Set.copyOf(classNames);

			Set<DeclaredType> ifaces = new HashSet<>();
			for (String c : classNames) {
				var elements = JavaLanguageModel.getInstance().getElements();
				var te = elements.getTypeElement(c);
				if (te == null) {
					continue;
				}
				var tm = te.asType();
				if (tm instanceof DeclaredType dt) {
					ifaces.add(dt);
				}
			}
			this.ifaces = Set.copyOf(ifaces);

			List<Pattern> patterns = new ArrayList<>();
			for (String p : classPatterns) {
				patterns.add(Pattern.compile(p));
			}
			this.patterns = List.copyOf(patterns);
		}

		public boolean isMatch(DeclaredType dt) {
			var erasedDt = JavaLanguageModel.getInstance().getTypes().erasure(dt);
			String className = erasedDt.toString();
			boolean match = isMatch(className);
			if (match)
				return true;
			for (var iface : ifaces) {
				match = JavaLanguageModel.getInstance().isSubtype(dt, iface);
				if (match) {
					return true;
				}
			}
			return false;
		}

		public boolean isMatch(String className) {
			if (classNames.isEmpty() && patterns.isEmpty()) {
				return true;
			}
			if (classNames.contains(className)) {
				return true;
			}
			for (var p : patterns) {
				if (p.matcher(className).matches()) {
					return true;
				}
			}
			return false;
		}

	}

	public enum FormatCallType {

		STACHE, JSTACHIO, JSTACHIO_BYTE

	}

}
