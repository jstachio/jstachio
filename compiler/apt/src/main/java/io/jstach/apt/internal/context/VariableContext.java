/*
 * Copyright (c) 2014, Victor Nazarov <asviraspossible@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.jstach.apt.internal.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Victor Nazarov
 */
public class VariableContext {

	public static String APPENDER = "appender";

	public static String ESCAPER = "escaper";

	public static String APPENDABLE = "unescapedWriter";

	public static String FORMATTER = "formatter";

	public static String TEMPLATE = "template";

	public static String CONTEXT = "context";

	public static String TEXT = "TEXT";

	public static VariableContext createDefaultContext(NullChecking nullChecking) {
		TreeMap<String, Integer> variables = new TreeMap<>();
		variables.put(ESCAPER, 1);
		variables.put(APPENDER, 1);
		variables.put(APPENDABLE, 1);
		variables.put(FORMATTER, 1);
		variables.put(TEMPLATE, 1);
		variables.put(CONTEXT, 1);

		return new RootVariableContext(APPENDER, ESCAPER, APPENDABLE, FORMATTER, TEMPLATE, CONTEXT, variables,
				nullChecking);
	}

	private final String appender;

	private final String escaper;

	private final String unescapedWriter;

	private final String formatter;

	private final String template;

	private final String context;

	private final Map<String, Integer> variables;

	private final @Nullable VariableContext parent;

	private final boolean escaped;

	private final NullChecking nullChecking;

	public enum NullChecking {

		ALWAYS, ANNOTATED;

		public boolean isNullable(JavaExpression expression) {
			return switch (this) {
				case ALWAYS -> true;
				case ANNOTATED -> expression.isNullable();
			};
		}

		public static NullChecking defaults() {
			return NullChecking.ALWAYS;
		}

		public boolean isDefault() {
			return NullChecking.ALWAYS == this;
		}

	}

	VariableContext(String appender, String escaper, String unescapedWriter, String formatter, String template,
			String context, Map<String, Integer> variables, @Nullable VariableContext parent, boolean escaped,
			NullChecking nullChecking) {
		this.appender = appender;
		this.escaper = escaper;
		this.unescapedWriter = unescapedWriter;
		this.formatter = formatter;
		this.template = template;
		this.context = context;
		this.variables = variables;
		this.parent = parent;
		this.escaped = escaped;
		this.nullChecking = nullChecking;
	}

	private static class RootVariableContext extends VariableContext {

		private List<String> textCodes = new ArrayList<>();

		RootVariableContext(String appender, String escaper, String unescapedWriter, String formatter, String template,
				String context, Map<String, Integer> variables, NullChecking nullChecking) {
			super(appender, escaper, unescapedWriter, formatter, template, context, variables, null, true,
					nullChecking);
		}

	}

	public List<String> textCodes() {
		var p = this;
		while (p != null) {
			if (p instanceof RootVariableContext r) {
				return r.textCodes;
			}
			p = p.parent;
		}
		throw new IllegalStateException("bug");
	}

	public List<Entry<String, String>> textVariables() {
		var codes = textCodes();
		List<Entry<String, String>> results = new ArrayList<>();
		int i = 0;
		for (var code : codes) {
			results.add(Map.entry(TEXT + "_" + i++, code));
		}
		return results;
	}

	public String addTextCode(String textCode) {
		var codes = textCodes();
		int i = codes.size();
		codes.add(textCode);
		return TEXT + "_" + i;
	}

	public String escaper() {
		return escaper;
	}

	public String appender() {
		return appender;
	}

	public String unescapedWriter() {
		return unescapedWriter;
	}

	public String formatter() {
		return formatter;
	}

	public boolean isEscaped() {
		return escaped;
	}

	public String context() {
		return context;
	}

	public String template() {
		return template;
	}

	public NullChecking nullChecking() {
		return nullChecking;
	}

	VariableContext unescaped() {
		return new VariableContext(appender, appender, unescapedWriter, formatter, template, context, variables, parent,
				false, nullChecking);
	}

	private @Nullable Integer lookupVariable(String baseName) {
		Integer result = variables.get(baseName);
		var p = parent;
		if (result != null || p == null)
			return result;
		else
			return p.lookupVariable(baseName);
	}

	public String introduceNewNameLike(String baseName) {
		int subscriptIndex = baseName.length();
		while (Character.isDigit(baseName.charAt(subscriptIndex - 1))) {
			subscriptIndex--;
		}
		if (subscriptIndex == baseName.length()) {
			Integer count = lookupVariable(baseName);
			if (count == null) {
				variables.put(baseName, 1);
				return baseName;
			}
			else {
				variables.put(baseName, count + 1);
				return baseName + count;
			}
		}
		else {
			Integer requestedCount = Integer.parseInt(baseName.substring(subscriptIndex));
			baseName = baseName.substring(0, subscriptIndex);
			Integer currentCount = lookupVariable(baseName);
			int count = currentCount == null || currentCount < requestedCount ? requestedCount : currentCount;
			variables.put(baseName, count + 1);
			return baseName + count;
		}
	}

	VariableContext createEnclosedContext() {
		return new VariableContext(appender, escaper, unescapedWriter, formatter, template, context,
				new TreeMap<String, Integer>(), this, true, nullChecking);
	}

}
