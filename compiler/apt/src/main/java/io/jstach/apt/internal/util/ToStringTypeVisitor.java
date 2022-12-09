package io.jstach.apt.internal.util;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.AbstractTypeVisitor14;

public class ToStringTypeVisitor extends AbstractTypeVisitor14<StringBuilder, StringBuilder> {

	private final int depth;

	private final boolean includeAnnotations;

	private final HashMap<TypeVariable, String> typeVariables;

	private static final boolean DEBUG = false;

	public static String toCodeSafeString(TypeMirror typeMirror) {
		return toCodeSafeString(typeMirror, 1, Map.of());
	}

	static String toCodeSafeString(TypeMirror typeMirror, int depth, Map<TypeVariable, String> typeVariables) {
		var v = new ToStringTypeVisitor(depth, typeVariables);
		v.typeVariables.putAll(typeVariables);
		StringBuilder b = new StringBuilder();
		return typeMirror.accept(v, b).toString();
	}

	private ToStringTypeVisitor() {
		this(1, new HashMap<>());
	}

	private ToStringTypeVisitor(int depth, Map<TypeVariable, String> typeVariables) {
		super();
		this.includeAnnotations = true;
		this.depth = depth;
		this.typeVariables = new HashMap<>();
		this.typeVariables.putAll(typeVariables);
	}

	void debug(String message, Object o) {
		if (DEBUG) {
			if (depth > 10) {
				throw new IllegalStateException();
			}
			System.out.println(indent() + "#" + message + ". " + o);
		}
	}

	String indent() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			sb.append("\t");
		}
		return sb.toString();
	}

	ToStringTypeVisitor child() {
		ToStringTypeVisitor tmv = new ToStringTypeVisitor(depth + 1, typeVariables);
		return tmv;
	}

	@Override
	public StringBuilder visitPrimitive(PrimitiveType t, StringBuilder p) {
		debug("primitive", t);

		if (includeAnnotations) {
			for (var ta : t.getAnnotationMirrors()) {
				p.append(ta.toString()).append(" ");
			}
		}
		p.append(t.getKind().toString().toLowerCase());

		return p;
	}

	@Override
	public StringBuilder visitNull(NullType t, StringBuilder p) {
		debug("null", t);
		return p;
	}

	@Override
	public StringBuilder visitArray(ArrayType t, StringBuilder p) {
		debug("array", t);
		var ct = t.getComponentType();
		ct.accept(child(), p);
		boolean first = true;
		if (includeAnnotations) {
			for (var ta : t.getAnnotationMirrors()) {
				if (first) {
					p.append(" ");
					first = false;
				}
				p.append(ta.toString()).append(" ");
			}
		}
		p.append("[]");
		return p;
	}

	@Override
	public StringBuilder visitDeclared(DeclaredType t, StringBuilder p) {
		debug("declared", t);
		// debug("enclosing type", t.getEnclosingType());
		String fqn = fullyQualfiedName(t, includeAnnotations);
		debug("typeUseFQN", fqn);
		p.append(fqn);
		var tas = t.getTypeArguments();
		if (!tas.isEmpty()) {
			p.append("<");
			for (var ta : t.getTypeArguments()) {
				ta.accept(child(), p);
			}
			p.append(">");
		}
		return p;
	}

	static String fullyQualfiedName(DeclaredType t, boolean includeAnnotations) {
		TypeElement element = (TypeElement) t.asElement();
		var typeUseAnnotations = t.getAnnotationMirrors();
		if (typeUseAnnotations.isEmpty() || !includeAnnotations) {
			return element.getQualifiedName().toString();
		}
		String enclosedPart;
		Element enclosed = element.getEnclosingElement();
		if (enclosed instanceof QualifiedNameable qn) {
			enclosedPart = qn.getQualifiedName().toString() + ".";
		}
		else {
			enclosedPart = "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(enclosedPart);
		for (var ta : typeUseAnnotations) {
			sb.append(ta.toString()).append(" ");
		}
		sb.append(element.getSimpleName());
		return sb.toString();
	}

	@Override
	public StringBuilder visitError(ErrorType t, StringBuilder p) {
		debug("error", t);
		return p;
	}

	@Override
	public StringBuilder visitTypeVariable(TypeVariable t, StringBuilder p) {
		debug("typeVariable", t);
		/*
		 * Types can be recursive so we have to check if we have already done this type.
		 */
		String previous = typeVariables.get(t);

		if (previous != null) {
			p.append(previous);
			return p;
		}
		debug("lower", t.getLowerBound());
		debug("upper", t.getUpperBound());
		StringBuilder sb = new StringBuilder();
		/*
		 * We do not have to print the upper and lower bound as those are defined usually
		 * on the method.
		 */
		if (includeAnnotations) {
			for (var ta : t.getAnnotationMirrors()) {
				p.append(ta.toString()).append(" ");
				sb.append(ta.toString()).append(" ");
			}
		}
		p.append(t.asElement().getSimpleName().toString());
		sb.append(t.asElement().getSimpleName().toString());
		typeVariables.put(t, sb.toString());

		// debug("upperCorrected", toCodeSafeString(t.getUpperBound(), depth + 1,
		// typeVariables));

		return p;
	}

	@Override
	public StringBuilder visitWildcard(WildcardType t, StringBuilder p) {
		debug("wildcard", t);
		var extendsBound = t.getExtendsBound();
		var superBound = t.getSuperBound();
		for (var ta : t.getAnnotationMirrors()) {
			p.append(ta.toString()).append(" ");
		}
		if (extendsBound != null) {
			p.append("? extends ");
			extendsBound.accept(child(), p);
		}
		else if (superBound != null) {
			p.append("? super ");
			superBound.accept(child(), p);
		}
		return p;
	}

	@Override
	public StringBuilder visitExecutable(ExecutableType t, StringBuilder p) {
		debug("executable", t);
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder visitNoType(NoType t, StringBuilder p) {
		debug("noType", t);
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder visitIntersection(IntersectionType t, StringBuilder p) {
		debug("intersection", t);
		boolean first = true;
		for (var b : t.getBounds()) {
			if (first) {
				first = false;
			}
			else {
				p.append("&");
			}
			b.accept(child(), p);
		}
		return p;
	}

	@Override
	public StringBuilder visitUnion(UnionType t, StringBuilder p) {
		debug("union", t);
		throw new UnsupportedOperationException();
	}

}
