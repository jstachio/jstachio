package io.jstach.apt.internal.context;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import org.eclipse.jdt.annotation.Nullable;

class OptionalRenderingContext implements RenderingContext {

	private final JavaExpression expression;

	private final TypeElement definitionElement;

	private final RenderingContext parent;

	OptionalRenderingContext(JavaExpression expression, TypeElement element, RenderingContext parent) {
		this.expression = expression;
		this.definitionElement = element;
		this.parent = parent;
	}

	@Override
	public String beginSectionRenderingCode() {
		return parent.beginSectionRenderingCode() + "if ( " + toNullableExpression().text() + " != null ) {";
	}

	@Override
	public String endSectionRenderingCode() {
		return parent.endSectionRenderingCode() + "}";
	}

	@Override
	public @Nullable JavaExpression get(String name) throws ContextException {
		return null;
	}

	@Override
	public JavaExpression currentExpression() {
		return toNullableExpression();
		// return expression;
	}

	private JavaExpression toNullableExpression() {
		var all = expression.model().getElements().getAllMembers(definitionElement);

		var getMethod = ElementFilter.methodsIn(all).stream()
				.filter(e -> "orElse".equals(e.getSimpleName().toString()) && e.getModifiers().contains(Modifier.PUBLIC)
						&& !e.getModifiers().contains(Modifier.STATIC) && e.getReturnType().getKind() != TypeKind.VOID
						&& e.getParameters().size() == 1)
				.findFirst().orElse(null);

		if (getMethod == null) {
			throw new IllegalStateException("bug in optional");
		}
		return expression.optionalOrElseNull(getMethod);
	}

	@Override
	public VariableContext createEnclosedVariableContext() {
		return parent.createEnclosedVariableContext();
	}

	// JavaExpression elementExpession() {
	// DeclaredType iterableType =
	// expression.model().getSupertype((DeclaredType)expression.type(),
	// expression.model().knownTypes()._Iterable);
	// TypeMirror elementType = iterableType.getTypeArguments().iterator().next();
	// if (elementType instanceof WildcardType) {
	// WildcardType wildcardType = (WildcardType)elementType;
	// elementType = wildcardType.getExtendsBound();
	// }
	// return expression.model().expression(elementVariableName, elementType);
	// }

	@Override
	public @Nullable RenderingContext getParent() {
		return this.parent;
	}

}
