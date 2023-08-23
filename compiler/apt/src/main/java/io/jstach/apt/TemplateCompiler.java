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
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.PartialParameterProcessor.Block;
import io.jstach.apt.WhitespaceTokenProcessor.ProcessToken.ProcessHint;
import io.jstach.apt.internal.AnnotatedException;
import io.jstach.apt.internal.CodeAppendable;
import io.jstach.apt.internal.CodeAppendable.StringCodeAppendable;
import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.MustacheToken.NewlineChar;
import io.jstach.apt.internal.MustacheToken.SpecialChar;
import io.jstach.apt.internal.MustacheToken.TagToken;
import io.jstach.apt.internal.MustacheTokenProcessor;
import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.TokenProcessor;
import io.jstach.apt.internal.context.ContextException;
import io.jstach.apt.internal.context.TemplateCompilerContext;
import io.jstach.apt.internal.context.TemplateCompilerContext.ContextType;
import io.jstach.apt.internal.context.TemplateCompilerContext.LambdaCompiler;
import io.jstach.apt.internal.context.TemplateStack;
import io.jstach.apt.internal.token.MustacheTagKind;
import io.jstach.apt.internal.token.MustacheTokenizer;
import io.jstach.apt.internal.util.ClassRef;
import io.jstach.apt.prism.Prisms;
import io.jstach.apt.prism.Prisms.Flag;

/**
 * @author Victor Nazarov
 */
abstract class TemplateCompiler extends AbstractTemplateCompiler {

	public static TemplateCompiler createCompiler(String templateName, TemplateLoader templateLoader,
			CodeAppendable writer, TemplateCompilerContext context, Set<Flag> flags) throws IOException {
		return new RootTemplateCompiler(templateName, templateLoader, writer, context, flags);
	}

	private final NamedReader reader;

	private TemplateCompilerContext context;

	private final LoggingSupport logging;

	int depth = 0;

	private final String codeTab = "    ";

	protected String baseCodeTab = codeTab + codeTab;

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

	private @Nullable ParameterPartial _partial;

	protected TemplateCompiler(NamedReader reader, TemplateCompilerContext context) {
		this.reader = reader;
		this.context = context;
		this.logging = context.getTemplateStack();
	}

	@Override
	public void run() throws ProcessingException, IOException {
		TokenProcessor<@Nullable Character> processor = MustacheTokenizer.createInstance(reader.name(), this);
		int readResult;
		while ((readResult = reader.read()) >= 0) {
			try {
				processor.processToken((char) readResult);
			}
			catch (ProcessingException e) {
				if (isDebug()) {
					String m = e.getMessage();
					if (m != null)
						debug(m);
					debug(context.getTemplateStack().describeTemplateStack());
					debug(context.printStack());
					debug(e);
				}
				throw e;
			}
		}
		processor.processToken(TokenProcessor.EOF);
		currentWriter().println();
	}

	@Override
	public LoggingSupport logging() {
		return this.logging;
	}

	@Override
	protected void processTokenGroup(List<ProcessToken> tokens) throws ProcessingException {
		var p = currentParameterPartial();
		if (inLambda()) {
			processInsideLambdaToken(tokens);
		}
		else if (p != null) {
			processInsideParameterPartial(tokens, true);
		}
		else if (containsParentSection(tokens)) {
			processInsideParameterPartial(tokens, false);
		}
		else {
			super.processTokenGroup(tokens);
		}
	}

	void processInsideLambdaToken(List<ProcessToken> tokens) throws ProcessingException {
		String lambdaName = context.currentEnclosedContextName();
		boolean finished = false;

		boolean standaloneEnd = !tokens.stream()
				.filter(t -> !(t.token().innerToken().isSectionEndToken(lambdaName) || t.hint() != ProcessHint.NORMAL))
				.findAny().isPresent();

		if (isDebug()) {
			debug("lambda = " + lambdaName + " standaloneEnd = " + standaloneEnd);
		}

		for (var t : tokens) {
			var mt = t.token().innerToken();
			if (mt.isSectionEndToken(lambdaName)) {
				finished = true;
				super._processToken(t.token());
			}
			else if (mt.isEOF()) {
				if (!finished) {
					throw new ProcessingException(position,
							"EOF reached before lambda closing tag found. lambda = \"" + lambdaName + "\"");
				}
			}
			else if (!standaloneEnd && !finished) {
				mt.appendRawText(rawLambdaContent);
				mt.appendEscapedJava(currentUnescaped);
			}
			else if (finished) {
				debug("finished token: ", t);
			}
		}
	}

	boolean containsParentSection(List<ProcessToken> tokens) {
		return tokens.stream().filter(t -> t.token().innerToken() instanceof TagToken tt
				&& tt.tagKind() == MustacheTagKind.BEGIN_PARENT_SECTION).findFirst().isPresent();
	}

	protected void processInsideParameterPartial(List<ProcessToken> tokens, boolean _insideParent)
			throws ProcessingException {
		MustacheTokenProcessor parentProcessor = new MustacheTokenProcessor() {
			boolean insideParent = _insideParent;

			@Override
			public void processToken(PositionedToken<MustacheToken> token) throws ProcessingException {
				var mt = token.innerToken();
				if (!insideParent && mt instanceof TagToken tt
						&& tt.tagKind() == MustacheTagKind.BEGIN_PARENT_SECTION) {
					insideParent = true;
					startParentSection(token);
				}
				else if (insideParent) {
					if (processInsideParameterPartial(token)) {
						insideParent = false;
					}
				}
				else {
					_processToken(token);
				}
			}
		};
		this.processTokenGroup(parentProcessor, tokens);
	}

	void startParentSection(PositionedToken<MustacheToken> token) throws ProcessingException {
		flushUnescaped();
		if (!(token.innerToken() instanceof TagToken tt)) {
			throw new IllegalStateException("bug");
		}
		String name = tt.name();
		var p = currentParameterPartial();
		if (p != null) {
			throw new IllegalStateException("parent (parameter partial) is already started for this context");
		}
		println();
		print("// start " + ContextType.PARENT_PARTIAL + ", template: " + name);
		println();
		try {
			p = createParameterPartial(name);
		}
		catch (IOException e) {
			throw new ProcessingException(position, e);
		}
		p.getProcessor().processToken(token);
		pushPartial(p);
	}

	boolean processInsideParameterPartial(PositionedToken<MustacheToken> token) throws ProcessingException {

		var p = currentParameterPartial();
		if (p == null)
			throw new IllegalStateException("bug");
		var processor = p.getProcessor();
		if (processor.isDone()) {
			throw new IllegalStateException("processor is already done");
		}
		beforeProcessToken(token);
		boolean done = processor.process(token).isDone();
		afterProcessToken(token);
		if (done) {
			try (p) {
				debug("start parameter partial: ", p.getTemplateName());
				p.run();
				debug("end parameter partial: ", p.getTemplateName());
			}
			catch (IOException e) {
				throw new ProcessingException(this.position, e);
			}
			debug("end content: ", p.getProcessor().getEndContent());
			popPartial();
			println();
			print("// end " + ContextType.PARENT_PARTIAL + ". template: " + p.getTemplateName());
			println();
		}
		return done;
	}

	protected boolean inLambda() {
		return context.getType() == ContextType.LAMBDA;
	}

	@Override
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
		return getWriter();
	}

	@Override
	public ParameterPartial createParameterPartial(String templateName) throws IOException {
		debug("Create Parameter Partial: " + templateName);
		var reader = getTemplateLoader().open(templateName);
		TemplateCompilerContext context = this.context.createForParameterPartial(templateName);
		/*
		 * TODO this is sloppy
		 */
		var c = new ParameterPartialTemplateCompiler(templateName, reader, this, context);
		var processor = c.processor;
		c.indent = partialIndent;
		partialIndent = "";
		return new ParameterPartial(c, processor);
	}

	public Partial createPartial(String templateName) throws IOException {
		var reader = getTemplateLoader().open(templateName);
		TemplateCompilerContext context = this.context.createForPartial(templateName);
		var c = new SubTemplateCompiler(reader, this, context) {
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
		print(context.renderUnescapedOutputCode(code));
		println();
	}

	/*
	 * TODO check is s.split(..., -1) will work here to make errorprone stop complaining.
	 * I'm aware of the surpring results of "split" but I already tested for the default
	 * behavior
	 */
	@SuppressWarnings("StringSplitter")
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
		currentWriter().print(baseCodeTab);
		for (int i = 0; i < depth; i++) {
			currentWriter().print(codeTab);
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
			pushContext(name, contextType);
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

	private void pushContext(String name, ContextType contextType) throws ContextException {
		context = context.getChild(name, contextType);
	}

	private void popContext() {
		context = context.parentContext();
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
				public String run(TemplateCompilerContext rootContext, Reader reader, Map<String, String> partials)
						throws IOException, ProcessingException {
					NamedReader namedReader = new NamedReader(reader, name, "INLINE");
					StringCodeAppendable codeAppendable = new StringCodeAppendable();
					try (var c = new SubTemplateCompiler(namedReader, self, rootContext) {
						{
							this.baseCodeTab = "";
						}

						@Override
						public TemplateCompilerType getCompilerType() {
							return TemplateCompilerType.LAMBDA;
						}

						@Override
						public CodeAppendable getWriter() {
							return codeAppendable;
						}

						@Override
						public TemplateLoader getTemplateLoader() {
							var rootLoader = super.getTemplateLoader();
							if (partials.isEmpty()) {
								return rootLoader;
							}
							return new TemplateLoader() {
								@Override
								public NamedReader open(String name) throws IOException {
									String t = partials.get(name);
									if (t != null) {
										return new NamedReader(new StringReader(t), name, "INLINE");
									}
									return rootLoader.open(name);
								}
							};
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
			pushContext(name, contextType);
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
		throw new IllegalStateException("bug");
	}

	@Override
	protected void _beginBlockSection(String name) throws ProcessingException {
		flushUnescaped();
		var contextType = ContextType.BLOCK;
		try {
			pushContext(name, contextType);
			printBeginSectionComment();
			// We do not increase the printing depth for blocks
			// depth++;
		}
		catch (ContextException e) {
			throw new ProcessingException(position, e);
		}
	}

	@Override
	protected void _endSection(String name) throws ProcessingException {
		if (!context.isEnclosed()) {
			throw new ProcessingException(position, "Closing \"" + name + "\" block when no block is currently open");
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
			case PATH, ESCAPED_VAR, UNESCAPED_VAR, SECTION_VAR, PARTIAL -> {
				throw new IllegalStateException("Context Type is wrong. " + context.getType());
			}
			case ROOT, SECTION, INVERTED -> {
				flushUnescaped();
				depth--;
			}
		}

		print(context.endSectionRenderingCode());
		printEndSectionComment();
		popContext();
	}

	protected void _endParentSection(String name) throws ProcessingException {
		/*
		 * We are at the end of a parent partial {{< parent}} {{/parent}} <-- we are here
		 */
		throw new UnsupportedOperationException("bug close parent should be dealt somewhere else. name=" + name);
	}

	@SuppressWarnings("UnusedVariable") // I want this for consistency
	protected void _endBlockSection(String name) {
		// do nothing
	}

	@Override
	protected void _variable(String name) throws ProcessingException.VariableProcessingException {
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
		catch (ContextException.TypeNotAllowedContextException ex) {
			String message = "Variable type not allowed (see @" + Prisms.JSTACHE_FORMATTER_TYPES_CLASS + ").";
			throw ProcessingException.VariableProcessingException.of(name, context, position, ex, message);
		}
		catch (ContextException.FieldNotFoundContextException ex) {
			String message = "Variable not found.";
			throw ProcessingException.VariableProcessingException.of(name, context, position, ex, message);
		}
		catch (ContextException ex) {
			String message = "Unknown variable problem.";
			throw ProcessingException.VariableProcessingException.of(name, context, position, ex, message);

		}
	}

	@Override
	protected void _partial(String name) throws ProcessingException {
		flushUnescaped();
		println();
		var contextType = ContextType.PARTIAL;
		try {
			pushContext(name, contextType);
			printBeginSectionComment();
			// We do not increase the printing depth
			// depth++;
			var pp = currentParameterPartial();
			if (pp != null) {
				throw new IllegalStateException("bug. parent (parameter partial) is already started for this context");
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
		popContext();
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

		private final TemplateStack templateStack;

		public RootTemplateCompiler(String templateName, TemplateLoader templateLoader, CodeAppendable writer,
				TemplateCompilerContext context, Set<Flag> flags) throws IOException {
			super(templateLoader.open(templateName), context);
			this.templateLoader = templateLoader;
			this.writer = writer;
			this.flags = flags;
			this.templateStack = context.getTemplateStack();
		}

		@Override
		public @Nullable TemplateCompilerLike getCaller() {
			return null;
		}

		@Override
		public ClassRef getModelClass() {
			return this.templateStack.getModelClass();
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

	static class SubTemplateCompiler extends TemplateCompiler implements TemplateCompilerLike.ChildTemplateCompiler {

		private final TemplateCompilerLike parent;

		protected SubTemplateCompiler(NamedReader reader, TemplateCompilerLike parent,
				TemplateCompilerContext context) {
			super(reader, context);
			this.parent = parent;
		}

		@Override
		public TemplateCompilerLike getCaller() {
			return this.parent;
		}

	}

	static class ParameterPartialTemplateCompiler extends SubTemplateCompiler {

		private final PartialParameterProcessor processor;

		private @Nullable Block block;

		private final Deque<Section<@Nullable Block>> sectionStack = new ArrayDeque<>();

		public ParameterPartialTemplateCompiler(String partialName, //
				NamedReader reader, //
				TemplateCompilerLike parent, //
				TemplateCompilerContext context) {
			super(reader, parent, context);
			this.processor = new PartialParameterProcessor(partialName, parent.logging());
		}

		@Override
		public TemplateCompilerType getCompilerType() {
			return TemplateCompilerType.PARAM_PARTIAL_TEMPLATE;
		}

		private @Nullable Block localBlock(String name) {
			return processor.getBlocks().get(name);
		}

		@SuppressWarnings("resource")
		public @Nullable Block findBlock(String name) {
			@Nullable
			ParameterPartialTemplateCompiler current = this;
			while (current != null) {
				var block = current.localBlock(name);
				if (block != null)
					return block;
				if (current.getCaller() instanceof ParameterPartialTemplateCompiler ppc) {
					current = ppc;
				}
				else {
					current = null;
					break;
				}
			}
			return null;
		}

		@Override
		protected List<PositionedToken<MustacheToken>> filter(PositionedToken<MustacheToken> positionedToken)
				throws ProcessingException {
			var mt = positionedToken.innerToken();
			if (mt instanceof TagToken tt) {
				final var b = block;
				if (tt.tagKind().isBeginSection()) {
					sectionStack.push(new Section<@Nullable Block>(tt, positionedToken.position(), b));
				}
				else if (tt.tagKind().isEndSection()) {
					var section = sectionStack.pop();
					if (!section.name().equals(tt.name())) {
						throw new ProcessingException(positionedToken.position(),
								"bad end section: \"" + tt.name() + "\" expected \"" + section.name() + "\"");
					}
				}
				if (b == null && tt.tagKind() == MustacheTagKind.BEGIN_BLOCK_SECTION) {
					String name = tt.name();
					Block _block = findBlock(name);
					this.block = _block;
					if (_block != null) {
						debug("Replaying tokens for block: " + name);
						return _block.tokens();
					}
					else {
						debug("No tokens found for block: " + name);
						return List.of(positionedToken);
					}
				}
				else if (b != null && tt.name().equals(b.name()) && tt.tagKind() == MustacheTagKind.END_SECTION) {
					var section = sectionStack.peek();
					if (section == null || section.data() == null) {
						debug("Closing block: " + b.name());
						debug("Section stack: " + sectionStack);
						block = null;
						return List.of();
					}
					else {
						debug("Processing inner block tag: " + tt.name());
						return List.of(positionedToken);
					}
				}
				else if (block != null) {
					debug("supressing tag: " + tt.name());
					return List.of();
				}
				else {
					return List.of(positionedToken);
				}
			}
			else if (block != null) {
				debug("supressing other: " + positionedToken.innerToken());
				return List.of();
			}
			else {
				return List.of(positionedToken);
			}
		}

	}

	interface Factory {

		TemplateCompiler createTemplateCompiler(NamedReader reader, CodeAppendable writer,
				TemplateCompilerContext context);

	}

}
