package io.jstach.apt.context;

import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.eclipse.jdt.annotation.Nullable;

public class EnumRenderingContext extends DeclaredTypeRenderingContext {

	private final TypeElement enumElement;

	public EnumRenderingContext(JavaExpression expression, TypeElement element, RenderingContext parent) {
		super(expression, element, parent);
		this.enumElement = element;
	}

	@Override
	public @Nullable JavaExpression get(String name) throws ContextException {

		/*
		 * This is a special care where an enum name is being used as a conditional
		 * section. {{#MyEnum.SOME_ENUM_NAME}} {{/MyEnum.SOME_ENUM_NAME}}
		 */

		/*
		 * Normal methods and fields still take precedences
		 */
		var pexp = super.get(name);

		if (pexp != null) {
			return pexp;
		}

		var names = getEnumValues(enumElement);

		if (names.contains(name)) {
			var exp = currentExpression();
			String bexp = enumElement.getQualifiedName().toString() + "." + name + " == " + exp.text();
			return exp.model().expression(bexp, exp.model().knownTypes()._boolean);

		}
		return null;
	}

	Set<String> getEnumValues(TypeElement enumTypeElement) {
		return enumTypeElement.getEnclosedElements().stream()
				.filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT)).map(Object::toString)
				.collect(Collectors.toSet());
	}

}
