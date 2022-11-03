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
package io.jstach.apt;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.AnnotatedException;
import io.jstach.apt.internal.CodeAppendable;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.TokenProcessor;
import io.jstach.apt.internal.CodeAppendable.HiddenCodeAppendable;
import io.jstach.apt.internal.CodeAppendable.StringCodeAppendable;
import io.jstach.apt.internal.MustacheToken.NewlineChar;
import io.jstach.apt.internal.MustacheToken.SpecialChar;
import io.jstach.apt.internal.MustacheToken.TagToken;
import io.jstach.apt.internal.context.ContextException;
import io.jstach.apt.internal.context.TemplateCompilerContext;
import io.jstach.apt.internal.context.TemplateCompilerContext.ContextType;
import io.jstach.apt.internal.context.TemplateCompilerContext.LambdaCompiler;
import io.jstach.apt.internal.token.MustacheTagKind;
import io.jstach.apt.internal.token.MustacheTokenizer;
import io.jstach.apt.prism.Prisms.Flag;

/**
 * @author Victor Nazarov
 */
class TemplateCompiler extends AbstractTemplateCompiler {

	public static TemplateCompiler createCompiler(String templateName, TemplateLoader templateLoader,
			CodeAppendable writer, TemplateCompilerContext context, TemplateCompilerType compilerType, Set<Flag> flags)
			throws IOException {

		return switch (compilerType) {
			case SIMPLE -> new SimpleTemplateCompiler(templateName, templateLoader, writer, context, flags);
			case PARTIAL_TEMPLATE, PARAM_PARTIAL_TEMPLATE, LAMBDA ->
				throw new IllegalArgumentException("Cannot create partial template as root");
		};
	}

	private final NamedReader reader;

	private TemplateCompilerContext context;

	int depth = 0;

	/*
	 * This buffer contains the raw uninterpolated unescaped markup as escaped java string
	 * literals.
	 *
	 * TODO maybe this should be a list of lines instead
	 */
	StringBuilder currentUnescaped = new StringBuilder();

	/*
	 * Unlike the above is not java escaped
	 */
	StringBuilder rawLambdaContent = new StringBuilder();

	String indent = "";

	private final @Nullable TemplateCompilerLike parent;

	private @Nullable ParameterPartial _partial;

	protected @Nullable StringCodeAppendable _currentBlockOutput;

	protected @Nullable HiddenCodeAppendable _parentBlockOutput;

	private TemplateCompiler(NamedReader reader, @Nullable TemplateCompilerLike parent,
			TemplateCompilerContext context) {
		this.reader = reader;
		this.parent = parent;
		this.context = context;
	}

	public void run() throws ProcessingException, IOException {
		TokenProcessor<@Nullable Character> processor = MustacheTokenizer.createInstance(reader.name(), this);
		int readResult;
		while ((readResult = reader.read()) >= 0) {
			try {
				processor.processToken((char) readResult);
			}
			catch (ProcessingException e) {
				if (isDebug()) {
					debug(e.getMessage());
					debug(context.printStack());
					e.printStackTrace();
				}
				throw e;
			}
		}
		processor.processToken(TokenProcessor.EOF);
		currentWriter().println();
	}

	@Override
	protected void processTokenGroup(List<ProcessToken> tokens) throws ProcessingException {
		if (inLambda()) {
			processInsideLambdaToken(tokens);
		}
		else {
			super.processTokenGroup(tokens);
		}
	}

	void processInsideLambdaToken(List<ProcessToken> tokens) throws ProcessingException {
		String lambdaName = context.currentEnclosedContextName();
		for (var t : tokens) {
			var mt = t.token().innerToken();
			if (mt instanceof TagToken tt && tt.tagKind() == MustacheTagKind.END_SECTION
					&& lambdaName.equals(tt.name())) {
				super._processToken(t.token());
			}
			else if (mt.isEOF()) {
				throw new ProcessingException(position,
						"EOF reached before lambda closing tag found. lambda = " + lambdaName);
			}
			else {
				mt.appendEscapedJava(currentUnescaped);
				mt.appendRawText(rawLambdaContent);
			}
		}

	}

	protected boolean inLambda() {
		return context.getType() == ContextType.LAMBDA;
	}

	@Override
	public @Nullable TemplateCompilerLike getCaller() {
		return this.parent;
	}

	public @Nullable ParameterPartial currentParameterPartial() {
		return this._partial;
	}

	void popPartial() {
		this._partial = null;
	}

	void pushPartial(ParameterPartial partial) {
		this._partial = partial;
	}

	@Override
	public TemplateCompilerType getCompilerType() {
		return TemplateCompilerType.SIMPLE;
	}

	@Override
	public String getTemplateName() {
		return reader.name();
	}

	public CodeAppendable currentWriter() {
		CodeAppendable out = _currentBlockOutput;
		if (out != null) {
			return out;
		}
		if ((out = _parentBlockOutput) != null) {
			return out;
		}
		return getWriter();
	}

	@Override
	public ParameterPartial createParameterPartial(String templateName) throws IOException {
		var reader = getTemplateLoader().open(templateName);
		TemplateCompilerContext context = this.context.createForParameterPartial(templateName);
		var c = new TemplateCompiler(reader, this, context) {
			@Override
			public TemplateCompilerType getCompilerType() {
				return TemplateCompilerType.PARAM_PARTIAL_TEMPLATE;
			}
		};
		c.indent = partialIndent;
		partialIndent = "";
		return new ParameterPartial(c);
	}

	public Partial createPartial(String templateName) throws IOException {
		var reader = getTemplateLoader().open(templateName);
		TemplateCompilerContext context = this.context.createForPartial(templateName);
		var c = new TemplateCompiler(reader, this, context) {
			@Override
			public TemplateCompilerType getCompilerType() {
				return TemplateCompilerType.PARTIAL_TEMPLATE;
			}
		};
		c.indent = partialIndent;
		partialIndent = "";
		return new Partial(c);
	}

	void flushUnescaped() {
		var code = currentUnescaped.toString();
		if (!code.isEmpty()) {
			_printCodeToWrite(code);
		}
		currentUnescaped.setLength(0);
	}

	private void printCodeToWrite(String s) {
		currentUnescaped.append(s);
	}

	private void _printCodeToWrite(String s) {
		if (s.isEmpty())
			return;
		String code = CodeAppendable.stringLiteralConcat(s);
		println();
		print(context.unescapedWriterExpression() + ".append(" + code + "); ");
		println();
	}

	private void print(String s) {
		int i = 0;
		for (String line : s.split("\n")) {
			if (i > 0) {
				println();
			}
			printIndent();
			currentWriter().print(line);
			i++;
		}
	}

	private void printIndent() {
		for (int i = 0; i <= depth + 1; i++) {
			currentWriter().print("    ");
		}
	}

	private void println() {
		currentWriter().println();
	}

	private void printBeginSectionComment() {
		println();
		print("// start " + context.getType() + ". name: " + context.currentEnclosedContextName() + ", template: "
				+ getTemplateName());
		println();
	}

	private void printEndSectionComment() {
		println();
		print("// end " + context.getType() + ". name: " + context.currentEnclosedContextName() + ", template: "
				+ getTemplateName());
		println();
	}

	@Override
	protected void _beginSection(String name) throws ProcessingException {
		flushUnescaped();
		var contextType = ContextType.SECTION;
		try {
			context = context.getChild(name, contextType);
			printBeginSectionComment();
			print(context.beginSectionRenderingCode());
			println();
			depth++;
			/*
			 * See if the context type is now a lambda
			 */
			if (context.getType() == ContextType.LAMBDA) {
				_beginLambdaSection(name);
			}

		}
		catch (ContextException ex) {
			throw new ProcessingException(position, ex);
		}
	}

	protected void _beginLambdaSection(String name) {
		if (isDebug()) {
			debug("Begin lambda. name = " + name);
		}
		rawLambdaContent.setLength(0);
	}

	protected void _endLambdaSection(String name) throws ProcessingException {
		if (isDebug()) {
			debug("End Lambda. name = " + name);
		}
		try {
			String javaCode = CodeAppendable.stringLiteralConcat(currentUnescaped.toString());
			currentUnescaped.setLength(0);
			String rawBody = rawLambdaContent.toString();
			rawLambdaContent.setLength(0);
			var self = this;
			LambdaCompiler lambdaCompiler = new LambdaCompiler() {
				@Override
				public String run(TemplateCompilerContext rootContext, Reader reader)
						throws IOException, ProcessingException {
					NamedReader namedReader = new NamedReader(reader, name, "INLINE");
					StringCodeAppendable codeAppendable = new StringCodeAppendable();
					try (var c = new TemplateCompiler(namedReader, self, rootContext) {
						@Override
						public TemplateCompilerType getCompilerType() {
							return TemplateCompilerType.LAMBDA;
						}

						public CodeAppendable getWriter() {
							return codeAppendable;
						}
					}) {
						c.run();
						return codeAppendable.toString();
					}

				}
			};
			print(context.lambdaRenderingCode(rawBody, javaCode, lambdaCompiler));
		}
		catch (ContextException ex) {
			throw new ProcessingException(position, ex);
		}
		catch (IOException ioe) {
			throw new ProcessingException(position, ioe);
		}
		catch (AnnotatedException ae) {
			throw new ProcessingException.AnnotationProcessingException(position, ae);
		}
	}

	@Override
	protected void _beginInvertedSection(String name) throws ProcessingException {
		flushUnescaped();
		var contextType = ContextType.INVERTED;
		try {
			context = context.getChild(name, contextType);
			printBeginSectionComment();
			print(context.beginSectionRenderingCode());
			println();
			depth++;
		}
		catch (ContextException ex) {
			throw new ProcessingException(position, ex);
		}
	}

	@Override
	protected void _beginParentSection(String name) throws ProcessingException {
		flushUnescaped();
		var contextType = ContextType.PARENT_PARTIAL;
		try {
			context = context.getChild(name, contextType);
			printBeginSectionComment();
			// We do not increase the printing depth
			// depth++;
			var p = currentParameterPartial();
			if (p != null) {
				throw new IllegalStateException("parent (parameter partial) is already started for this context");
			}
			p = createParameterPartial(name);
			pushPartial(p);
			_parentBlockOutput = new HiddenCodeAppendable(s -> {
				/* if (isDebug()) { debug(s);} */
			});

		}
		catch (ContextException | IOException ex) {
			throw new ProcessingException(position, ex);
		}
	}

	@Override
	protected void _beginBlockSection(String name) throws ProcessingException {
		flushUnescaped();
		var contextType = ContextType.BLOCK;
		try {
			context = context.getChild(name, contextType);
			printBeginSectionComment();
			// We do not increase the printing depth for blocks
			// depth++;
		}
		catch (ContextException e) {
			throw new ProcessingException(position, e);
		}
		var parameterPartial = currentParameterPartial();
		var caller = getCaller();

		var templateType = getCompilerType();

		if (parameterPartial != null) {
			/*
			 * {{< parent}} {{$block}} <-- We are here some content {{/block}} {{/parent}}
			 */
			if (parameterPartial.getBlockArgs().containsKey(name)) {
				throw new ProcessingException(position, "parameter block was defined earlier. block = " + name);
			}
			var writer = new StringCodeAppendable();
			parameterPartial.getBlockArgs().put(name, writer);
			if (_currentBlockOutput != null) {
				throw new IllegalStateException("existing block output. template: " + getTemplateName());
			}
			_currentBlockOutput = writer;
			if (currentWriter() != _currentBlockOutput) {
				throw new IllegalStateException("unexpected current writer");
			}
			// println();
			print("// start BLOCK parameter. name: \"" + name + "\", template: " + getTemplateName() + ", partial: "
					+ parameterPartial.getTemplateName());
			println();
		}
		else if (templateType == TemplateCompilerType.PARAM_PARTIAL_TEMPLATE && caller != null) {
			/*
			 * We are in a block in a partial template e.g. partial.mustache
			 * {{$block}}{{/block}}
			 */
			if (getCompilerType() == TemplateCompilerType.PARAM_PARTIAL_TEMPLATE
					&& caller.currentParameterPartial() == null) {
				throw new IllegalStateException("bug. missing partial parameter info");
			}
			if (_currentBlockOutput != null) {
				throw new IllegalStateException(
						"existing block output. template: " + getTemplateName() + " name: " + name);
			}
			/*
			 * We will reconcile at the endSection if we actually need the output
			 */
			_currentBlockOutput = new StringCodeAppendable();
			// println();
			print("// start BLOCK default. name: \"" + name + "\", template: " + getTemplateName());
			println();
		}
		else {
			/*
			 * {{$block}}{{/block}}
			 */
			// Apparently this either a root or partial template has block parameters.
			// We do nothing for now
			// println();
			print("// unused block: " + name);
			println();
		}
	}

	@Override
	protected void _endSection(String name) throws ProcessingException {
		if (!context.isEnclosed()) {
			throw new ProcessingException(position, "Closing " + name + " block when no block is currently open");
		}
		if (!context.currentEnclosedContextName().equals(name)) {
			throw new ProcessingException(position,
					"Closing " + name + " block instead of " + context.currentEnclosedContextName());
		}
		var contextType = context.getType();
		switch (contextType) {
			case LAMBDA -> {
				_endLambdaSection(name);
				depth--;
			}
			case PARENT_PARTIAL -> {
				flushUnescaped();
				_endParentSection(name);
			}
			case BLOCK -> {
				flushUnescaped();
				_endBlockSection(name);
			}
			case PATH, ESCAPED_VAR, UNESCAPED_VAR, PARTIAL -> {
				throw new IllegalStateException("Context Type is wrong. " + context.getType());
			}
			case ROOT, SECTION, INVERTED -> {
				flushUnescaped();
				depth--;
			}
		}
		;
		print(context.endSectionRenderingCode());
		printEndSectionComment();
		context = context.parentContext();
	}

	private void _endParentSection(String name) throws ProcessingException {
		/*
		 * We are at the end of a parent partial {{< parent}} {{/parent}} <-- we are here
		 */
		_parentBlockOutput = null;
		var p = currentParameterPartial();
		if (p == null) {
			throw new IllegalStateException("partial is has not started for this context");
		}
		try (p) {
			if (isDebug()) {
				debug("Running partial. " + p);
			}
			p.run();
			popPartial();
		}
		catch (IOException e) {
			throw new ProcessingException(position, e);
		}
	}

	private void _endBlockSection(String name) {
		// Block END
		switch (getCompilerType()) {
			case PARAM_PARTIAL_TEMPLATE -> {
				/*
				 * We are in a partial template at the end of a block {{$block}}
				 * {{/block}} <-- we are here
				 */
				var callingTemplate = getCaller();
				if (callingTemplate == null) {
					throw new IllegalStateException("missing calling template");
				}
				StringCodeAppendable output = _currentBlockOutput;
				if (output == null) {
					throw new IllegalStateException("Missing block output");
				}

				ParameterPartial callingPartial = callingTemplate.currentParameterPartial();
				if (callingPartial == null) {
					throw new IllegalStateException("missing partial info");
				}
				var callingBlock = callingPartial.findBlock(name); // callingPartial.getBlockArgs().get(name);
				if (callingBlock != null) {
					output = callingBlock;
				}
				_currentBlockOutput = null;
				/*
				 * We dump the generated code to the class file being generated.
				 */
				currentWriter().print(output.toString());
				println();
				if (callingBlock != null) {
					print("// end BLOCK parameter. name: \"" + name + "\", template: "
							+ callingTemplate.getTemplateName() + ", partial: " + callingPartial.getTemplateName());
				}
				else {
					print("// end BLOCK default. name: \"" + name + "\", template: " + getTemplateName() + ", partial: "
							+ callingPartial.getTemplateName());
				}
			}
			case SIMPLE, PARTIAL_TEMPLATE, LAMBDA -> {
				/*
				 * We are in the caller template at the end of a block {{$block}}
				 * {{/block}} <-- we are here
				 */
				var p = currentParameterPartial();
				if (p != null) {
					/*
					 * We are inside of some {{< parent }} and the block is done so we can
					 * restore output
					 */
					if (_currentBlockOutput == null) {
						throw new IllegalStateException("should be capturing for the block");
					}
					if (_currentBlockOutput != p.getBlockArgs().get(name)) {
						throw new IllegalStateException();
					}
					_currentBlockOutput = null;
				}
			}
		}
	}

	@Override
	protected void _variable(String name) throws ProcessingException {
		indent();
		flushUnescaped();
		println();
		try {
			// TODO figure out indenting variables
			TemplateCompilerContext variable = context.getChild(name, ContextType.ESCAPED_VAR);
			print("// variable: " + variable.currentEnclosedContextName());
			println();
			print(variable.renderingCode());
			println();

		}
		catch (ContextException ex) {
			var templateStack = context.getTemplateStack();
			String message = "Variable not found." + " var: " + name + ", template: "
					+ templateStack.describeTemplateStack() + " context stack: " + context.printStack() + "\n";
			throw new ProcessingException.VariableNotFoundProcessingException(position, ex, message);
		}
	}

	@Override
	protected void _partial(String name) throws ProcessingException {
		flushUnescaped();
		println();
		var contextType = ContextType.PARTIAL;
		try {
			context = context.getChild(name, contextType);
			printBeginSectionComment();
			// We do not increase the printing depth
			// depth++;
			var pp = currentParameterPartial();
			if (pp != null) {
				throw new IllegalStateException("parent (parameter partial) is already started for this context");
			}
			try (var p = createPartial(name)) {
				p.run();
			}

		}
		catch (ContextException | IOException ex) {
			throw new ProcessingException(position, ex);
		}
		print(context.endSectionRenderingCode());
		printEndSectionComment();
		context = context.parentContext();
	}

	@Override
	protected void _unescapedVariable(String name) throws ProcessingException {
		indent();
		flushUnescaped();
		println();
		try {
			TemplateCompilerContext variable = context.getChild(name, ContextType.UNESCAPED_VAR);
			print("// unescaped variable: " + variable.currentEnclosedContextName());
			println();

			print(variable.unescapedRenderingCode());
			println();
		}
		catch (ContextException ex) {
			throw new ProcessingException(position, ex);
		}
	}

	private void indent() {
		if (atStartOfLine) {
			printCodeToWrite(indent);
		}
	}

	@Override
	protected void _specialCharacter(SpecialChar specialChar) throws ProcessingException {
		printCodeToWrite(specialChar.javaEscaped());
	}

	@Override
	public void _newline(NewlineChar c) throws ProcessingException {
		printCodeToWrite(c.javaEscaped());
	}

	@Override
	public void _text(String s) throws ProcessingException {
		indent();
		printCodeToWrite(s);
	}

	@Override
	public void _endOfFile() throws ProcessingException {
		flushUnescaped();
		if (!context.isEnclosed())
			return;
		else {
			throw new ProcessingException(position,
					"Unclosed \"" + context.currentEnclosedContextName() + "\" block at end of file");
		}
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	static class RootTemplateCompiler extends TemplateCompiler {

		private final TemplateLoader templateLoader;

		private final CodeAppendable writer;

		private final Set<Flag> flags;

		public RootTemplateCompiler(String templateName, TemplateLoader templateLoader, CodeAppendable writer,
				TemplateCompilerContext context, Set<Flag> flags) throws IOException {
			super(templateLoader.open(templateName), null, context);
			this.templateLoader = templateLoader;
			this.writer = writer;
			this.flags = flags;
		}

		@Override
		public @Nullable TemplateCompilerLike getCaller() {
			return null;
		}

		@Override
		public TemplateLoader getTemplateLoader() {
			return this.templateLoader;
		}

		@Override
		public CodeAppendable getWriter() {
			return this.writer;
		}

		@Override
		public Set<Flag> flags() {
			return this.flags;
		}

	}

	static class SimpleTemplateCompiler extends RootTemplateCompiler {

		private SimpleTemplateCompiler(String templateName, TemplateLoader templateLoader, CodeAppendable writer,
				TemplateCompilerContext context, Set<Flag> flags) throws IOException {
			super(templateName, templateLoader, writer, context, flags);
		}

		@Override
		public void run() throws ProcessingException, IOException {
			boolean suppressesOutput = getWriter().suppressesOutput();
			getWriter().enableOutput();
			super.run();
			if (suppressesOutput)
				getWriter().disableOutput();
			else
				getWriter().enableOutput();
		}

	}

	interface Factory {

		TemplateCompiler createTemplateCompiler(NamedReader reader, SwitchablePrintWriter writer,
				TemplateCompilerContext context);

	}

}
