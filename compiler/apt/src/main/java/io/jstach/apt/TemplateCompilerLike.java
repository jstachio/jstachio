package io.jstach.apt;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.CodeAppendable;
import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.util.ClassRef;
import io.jstach.apt.prism.Prisms.Flag;

interface TemplateCompilerLike extends AutoCloseable {

	void run() throws ProcessingException, IOException;

	void close() throws IOException;

	TemplateCompilerType getCompilerType();

	LoggingSupport logging();

	String getTemplateName();

	default ClassRef getModelClass() {
		return Objects.requireNonNull(getCaller()).getModelClass();
	}

	@Nullable
	TemplateCompilerLike getCaller();

	default TemplateLoader getTemplateLoader() {
		return Objects.requireNonNull(getCaller()).getTemplateLoader();
	}

	default CodeAppendable getWriter() {
		return Objects.requireNonNull(getCaller()).getWriter();
	}

	default Set<Flag> flags() {
		return Objects.requireNonNull(getCaller()).flags();
	}

	@Nullable
	ParameterPartial currentParameterPartial();

	ParameterPartial createParameterPartial(String templateName) throws ProcessingException, IOException;

	/*
	 * TODO rename to TemplateType
	 */
	public enum TemplateCompilerType {

		SIMPLE, //
		LAMBDA, //
		PARTIAL_TEMPLATE, //
		PARAM_PARTIAL_TEMPLATE; // aka parent aka {{< parent }}

	}

	interface TemplateLoader {

		NamedReader open(String name) throws IOException;

	}

	abstract class AbstractPartial implements AutoCloseable {

		protected final TemplateCompilerLike templateCompiler;

		public AbstractPartial(TemplateCompilerLike templateCompiler) {
			super();
			this.templateCompiler = templateCompiler;
		}

		public String getTemplateName() {
			return templateCompiler.getTemplateName();
		}

		void run() throws ProcessingException, IOException {
			templateCompiler.run();
		}

		@Override
		public void close() throws IOException {
			templateCompiler.close();
		}

	}

	class Partial extends AbstractPartial {

		public Partial(TemplateCompilerLike templateCompiler) {
			super(templateCompiler);
		}

		@Override
		public String toString() {
			return "Partial(template = " + getTemplateName() + ")";
		}

	}

	class ParameterPartial extends AbstractPartial {

		private final PartialParameterProcessor processor;

		private boolean ran;

		public ParameterPartial(TemplateCompilerLike templateCompiler, PartialParameterProcessor processor) {
			super(templateCompiler);
			this.processor = processor;
		}

		public PartialParameterProcessor getProcessor() {
			return processor;
		}

		@Override
		void run() throws ProcessingException, IOException {
			if (ran) {
				throw new IllegalStateException("Already ran");
			}
			ran = true;
			super.run();
		}

	}

}