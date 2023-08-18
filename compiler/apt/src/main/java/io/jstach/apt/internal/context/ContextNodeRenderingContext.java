package io.jstach.apt.internal.context;

import java.util.function.Predicate;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import org.eclipse.jdt.annotation.Nullable;

public class ContextNodeRenderingContext extends MapRenderingContext {

	ContextNodeRenderingContext(JavaExpression expression, TypeElement element, RenderingContext parent) {
		super(expression, element, parent);
	}

	@Override
	public @Nullable JavaExpression find(String name, Predicate<RenderingContext> filter) throws ContextException {
		// See MapNode.find
		// Currently this only works if MapNode is the root context

		var parent = getParent();

		JavaExpression r = parent == null ? null
				: parent.find(name, filter.and(c -> !(c instanceof MapRenderingContext)));
		if (r != null) {
			return r;
		}
		if (!filter.test(this)) {
			return null;
		}

		var all = JavaLanguageModel.getInstance().getElements().getAllMembers(definitionElement);

		var getMethod = ElementFilter.methodsIn(all).stream()
				.filter(e -> "find".equals(e.getSimpleName().toString()) && e.getModifiers().contains(Modifier.PUBLIC)
						&& !e.getModifiers().contains(Modifier.STATIC) && e.getReturnType().getKind() != TypeKind.VOID
						&& e.getParameters().size() == 1)
				.findFirst().orElse(null);

		if (getMethod == null) {
			return null;
		}
		var returnedExpression = expression.mapGet(getMethod, name);
		return returnedExpression;
	}

}
