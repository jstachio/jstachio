package io.jstach.apt;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.token.Delimiters;
import io.jstach.apt.internal.MustacheToken.NewlineChar;
import io.jstach.apt.internal.MustacheToken.SpecialChar;

class CompilingTokenProcessor implements MustacheToken.Visitor<@Nullable Void, ProcessingException> {

	private final AbstractTemplateCompiler templateCompiler;

	public CompilingTokenProcessor(AbstractTemplateCompiler templateCompiler) {
		this.templateCompiler = templateCompiler;
	}

	@Override
	public @Nullable Void beginSection(String name) throws ProcessingException {
		templateCompiler._beginSection(name);
		return null;

	}

	@Override
	public @Nullable Void beginInvertedSection(String name) throws ProcessingException {
		templateCompiler._beginInvertedSection(name);
		return null;
	}

	@Override
	public @Nullable Void beginParentSection(String name) throws ProcessingException {
		templateCompiler._beginParentSection(name);
		return null;
	}

	@Override
	public @Nullable Void beginBlockSection(String name) throws ProcessingException {
		templateCompiler._beginBlockSection(name);
		return null;
	}

	@Override
	public @Nullable Void endSection(String name) throws ProcessingException {
		templateCompiler._endSection(name);
		return null;
	}

	@Override
	public @Nullable Void variable(String name) throws ProcessingException {
		templateCompiler._variable(name);
		return null;
	}

	public @Nullable Void partial(String name) throws ProcessingException {
		templateCompiler._partial(name);
		return null;
	}

	@Override
	public @Nullable Void unescapedVariable(String name) throws ProcessingException {
		templateCompiler._unescapedVariable(name);
		return null;
	}

	public @Nullable Void specialCharacter(SpecialChar specialChar) throws ProcessingException {
		templateCompiler._specialCharacter(specialChar);
		return null;
	}

	public @Nullable Void newline(NewlineChar c) throws ProcessingException {
		templateCompiler._newline(c);
		return null;
	};

	@Override
	public @Nullable Void text(String s) throws ProcessingException {
		templateCompiler._text(s);
		return null;
	}

	@Override
	public @Nullable Void endOfFile() throws ProcessingException {
		templateCompiler._endOfFile();
		return null;
	}

	@Override
	public @Nullable Void delimiters(Delimiters delimiters) throws ProcessingException {
		return null;
	}

	@Override
	public @Nullable Void comment(String comment) throws ProcessingException {
		return null;
	}

}