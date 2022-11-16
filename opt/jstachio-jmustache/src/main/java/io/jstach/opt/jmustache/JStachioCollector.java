package io.jstach.opt.jmustache;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;

import com.samskivert.mustache.BasicCollector;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.VariableFetcher;
import com.samskivert.mustache.Template.Fragment;

import io.jstach.annotation.JStacheLambda;

class JStachioCollector extends BasicCollector {

	@Override
	public Mustache.@Nullable VariableFetcher createFetcher(Object ctx, String name) {
		Mustache.VariableFetcher fetcher = super.createFetcher(ctx, name);
		if (fetcher != null)
			return fetcher;

		// first check for a getter which provides the value
		Class<?> cclass = ctx.getClass();

		Method m = getMethod(cclass, name);
		if (m == null) {
			m = getIfaceMethod(cclass, name);
		}
		if (m == null) {
			m = getLambdaMethod(cclass, name);
		}
		if (m != null) {
			return new MethodFetcher(m);
		}

		if (EnumNameFetcher.isEnumName(ctx, name)) {
			return new EnumNameFetcher();
		}

		return null;
	}

	private static class MethodFetcher implements VariableFetcher {

		private final Method method;

		public MethodFetcher(Method method) {
			super();
			this.method = method;
		}

		@Override
		public Object get(Object ctx, String name) throws Exception {
			var lambda = maybeLambda(method, ctx);
			if (lambda != null) {
				return lambda;
			}
			return method.invoke(ctx);
		}

	}

	private static class EnumNameFetcher implements VariableFetcher {

		@Override
		public Object get(Object ctx, String name) throws Exception {
			return isEnumName(ctx, name);
		}

		static boolean isEnumName(Object ctx, String name) {
			if (ctx instanceof Enum<?> e && e.name().equals(name)) {
				return true;
			}
			return false;
		}

	}

	static Mustache.@Nullable Lambda maybeLambda(Method method, Object lambdaOwner) {
		var annotation = method.getAnnotation(JStacheLambda.class);
		if (annotation == null) {
			return null;
		}
		return new Mustache.Lambda() {

			@Override
			public void execute(Fragment frag, Writer out) throws IOException {
				var parameters = method.getParameters();
				var context = frag.context();
				String template = frag.decompile();
				List<Object> args = new ArrayList<>();

				for (var p : parameters) {
					boolean raw = p.getAnnotation(JStacheLambda.Raw.class) != null;
					if (raw) {
						args.add(template);
					}
					else {
						args.add(context);
					}
				}
				boolean raw = method.getAnnotation(JStacheLambda.Raw.class) != null;
				try {
					Object result = method.invoke(lambdaOwner, args.toArray());
					if (raw) {
						out.append((String) result);
					}
					else {
						frag.execute(result, out);
					}
				}
				catch (Exception e) {
					throw new RuntimeException("Failed to execute lambda: " + method, e);
				}

			}
		};
	}

	@Nullable
	Method getLambdaMethod(Class<?> clazz, String name) {
		return Stream.of(clazz.getMethods()).filter(m -> name.equals(m.getName()))
				.filter(m -> m.getAnnotation(JStacheLambda.class) != null).findFirst().orElse(null);
	}

	Set<Class<?>> findSupers(Class<?> clazz) {

		Set<Class<?>> ifaces = new LinkedHashSet<Class<?>>();

		for (Class<?> cc = clazz; cc != null && cc != Object.class; cc = cc.getSuperclass()) {
			ifaces.add(cc);
		}

		for (Class<?> cc = clazz; cc != null && cc != Object.class; cc = cc.getSuperclass()) {
			addIfaces(ifaces, cc, false);
		}

		return ifaces;
	}

	protected @Nullable Method getIfaceMethod(Class<?> clazz, String name) {
		// enumerate the transitive closure of all interfaces implemented by
		// clazz
		Set<Class<?>> ifaces = new LinkedHashSet<Class<?>>();
		for (Class<?> cc = clazz; cc != null && cc != Object.class; cc = cc.getSuperclass()) {
			addIfaces(ifaces, cc, false);
		}
		// now search those in the order that we found them
		for (Class<?> iface : ifaces) {
			Method m = getMethod(iface, name, true);
			if (m != null)
				return m;
		}
		return null;
	}

	private void addIfaces(Set<Class<?>> ifaces, Class<?> clazz, boolean isIface) {
		if (isIface)
			ifaces.add(clazz);
		for (Class<?> iface : clazz.getInterfaces())
			addIfaces(ifaces, iface, true);
	}

	protected @Nullable Method getMethod(Class<?> clazz, String name) {
		for (Class<?> cc = clazz; cc != null && cc != Object.class; cc = cc.getSuperclass()) {
			Method m = getMethod(cc, name, true);
			if (m != null)
				return m;
		}
		return null;
	}

	protected @Nullable Method getMethod(Class<?> clazz, String name, boolean declared) {

		if (declared && !Modifier.isPublic(clazz.getModifiers())) {
			return null;
		}

		String upperName = Character.toUpperCase(name.charAt(0)) + name.substring(1);

		Method m = maybeMethod(clazz, "get" + upperName, declared);
		if (m != null) {
			return m;
		}

		m = maybeMethod(clazz, "is" + upperName, declared);

		if (m != null && (m.getReturnType().equals(boolean.class) || m.getReturnType().equals(Boolean.class))) {
			return m;
		}

		return maybeMethod(clazz, name, declared);

	}

	@Nullable
	Method maybeMethod(Class<?> clazz, String name, boolean declared) {
		if (declared && !Modifier.isPublic(clazz.getModifiers())) {
			return null;
		}
		try {
			Method m = declared ? clazz.getDeclaredMethod(name) : clazz.getMethod(name);
			if (isValidMethod(m)) {
				return m;
			}
		}
		catch (Exception e) {
			// fall through
		}
		return null;
	}

	protected boolean isValidMethod(Method m) {
		return Modifier.isPublic(m.getModifiers()) && !m.getReturnType().equals(void.class);
	}

	@Override
	public <K, V> Map<K, V> createFetcherCache() {
		return new ConcurrentHashMap<>();
	}

}
