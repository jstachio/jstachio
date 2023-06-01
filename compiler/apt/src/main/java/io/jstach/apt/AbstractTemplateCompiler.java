package io.jstach.apt;

import io.jstach.apt.internal.LoggingSupport.LoggingSupplier;
import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.MustacheToken.NewlineChar;
import io.jstach.apt.internal.MustacheToken.SpecialChar;
import io.jstach.apt.internal.MustacheTokenProcessor;
import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;

abstract class AbstractTemplateCompiler extends WhitespaceTokenProcessor
		implements TemplateCompilerLike, MustacheTokenProcessor, LoggingSupplier {

	@Override
	protected void handleToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
		positionedToken.innerToken().accept(new CompilingTokenProcessor(this));
	}

	protected abstract void _endOfFile() throws ProcessingException;

	protected abstract void _text(String s) throws ProcessingException;

	protected abstract void _newline(NewlineChar c) throws ProcessingException;

	protected abstract void _specialCharacter(SpecialChar specialChar) throws ProcessingException;

	protected abstract void _unescapedVariable(String name) throws ProcessingException;

	protected abstract void _partial(String name) throws ProcessingException;

	protected abstract void _variable(String name) throws ProcessingException;

	protected abstract void _endSection(String name) throws ProcessingException;

	protected abstract void _beginBlockSection(String name) throws ProcessingException;

	protected abstract void _beginParentSection(String name) throws ProcessingException;

	protected abstract void _beginInvertedSection(String name) throws ProcessingException;

	protected abstract void _beginSection(String name) throws ProcessingException;

}
