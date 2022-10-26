package io.jstach.apt.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.annotation.JStacheLambda;
import io.jstach.apt.AnnotatedException;
import io.jstach.apt.prism.RawPrism;

public sealed interface Lambda {

	default String name() {
		return method().name();
	}

	public Method method();

	default JavaExpression callExpression(String literalBlock, LambdaContext context) throws TypeException {
		JavaLanguageModel model = method().expression().model();
		var currentContextExpression = context.get();
		List<JavaExpression> args = new ArrayList<>();
		for (var param : method().params()) {
			JavaExpression arg = switch (param.paramType()) {
				case STRING_BODY -> {
					yield currentContextExpression.stringLiteral(literalBlock);
				}
				case CURRENT_CONTEXT -> {
					var supertype = param.type();
					if (!model.isSubtype(currentContextExpression.type(), supertype)) {
						var method = method();
						throw new TypeException(String.format("""
								Lambda context parameter is incorrect. details:
								                         method :  %s
								            current context type:  %s
								    lambda expected context type:  %s
								""", method.methodElement(), currentContextExpression.type(), param.type()));
					}
					yield currentContextExpression;
				}
			};
			args.add(arg);
		}

		return method().expression().methodCall(method().methodElement(), args.toArray(new JavaExpression[] {}));
	}

	public enum ReturnType {

		RAW_STRING, MODEL

	}

	public enum ParamType {

		STRING_BODY, CURRENT_CONTEXT

	}

	public record Param(String name, ParamType paramType, TypeMirror type) {
	}

	public record Method(JavaExpression expression, String name, ExecutableElement methodElement, ReturnType returnType,
			List<Param> params) {

		public static Method of(JavaExpression expression, ExecutableElement method, @Nullable String name)
				throws AnnotatedException {
			if (name == null || name.isBlank()) {
				name = method.getSimpleName().toString();
			}

			var model = expression.model();

			var parameters = method.getParameters();
			if (parameters.size() > 2 || parameters.size() < 1) {
				throw new UnsupportedOperationException("Lambda can only support 1 or 2 parameters");
			}
			List<Param> params = new ArrayList<>();
			var it = parameters.iterator();
			VariableElement p = it.next();
			boolean raw = RawPrism.getInstanceOn(p) != null;

			if (raw && model.isType(p.asType(), model.knownTypes()._String)) {
				params.add(new Param(name, ParamType.STRING_BODY, p.asType()));
			}
			else if (raw) {
				throw new AnnotatedException(p, "Only String types can be annotated with Raw");
			}
			else {
				params.add(new Param(name, ParamType.CURRENT_CONTEXT, p.asType()));
				if (it.hasNext()) {
					throw new UnsupportedOperationException("Lambdas can only have one context parameter");
				}
			}
			if (it.hasNext()) {
				p = it.next();
				params.add(new Param(name, ParamType.CURRENT_CONTEXT, p.asType()));
			}

			ReturnType returnType;
			raw = RawPrism.getInstanceOn(method) != null;
			if (raw && model.isType(method.getReturnType(), model.knownTypes()._String)) {
				returnType = ReturnType.RAW_STRING;
			}
			else if (raw) {
				throw new AnnotatedException(method, "Only String return types can be annotated with Raw");
			}
			else if (method.getReturnType() instanceof DeclaredType dt) {
				returnType = ReturnType.MODEL;
			}
			else {
				throw new UnsupportedOperationException(
						"Currently only raw String and model Class return types are supported.");
			}
			return new Method(expression, name, method, returnType, params);
		}
	}

	// public record InlineTemplateLambda(
	// JavaExpression expression,
	// ExecutableElement method,
	// String name,
	// String template) implements Lambda {
	//
	// }
	//
	// public record PathTemplateLambda(
	// JavaExpression expression,
	// ExecutableElement method,
	// String name,
	// String path) implements Lambda {
	//
	// }

	record SimpleLambda(Method method) implements Lambda {
	}

	public static Lambda of( //
			JavaExpression expression, ExecutableElement method, @Nullable String name) throws AnnotatedException {
		if (name == null || name.isBlank()) {
			name = method.getSimpleName().toString();
		}

		Method m = Method.of(expression, method, name);
		return new SimpleLambda(m);
	}

	public class Lambdas {

		private final Map<String, Lambda> lambdas;

		public Lambdas(Map<String, Lambda> lambdas) {
			super();
			this.lambdas = lambdas;
		}

		public Map<String, Lambda> lambdas() {
			return lambdas;
		}

	}

}
