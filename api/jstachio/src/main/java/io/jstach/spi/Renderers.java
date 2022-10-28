package io.jstach.spi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import io.jstach.Renderer;
import io.jstach.annotation.JStache;

class Renderers {

	private static final String IMPLEMENTATION_SUFFIX = "Renderer";

	private Renderers() {
	}

	public static <T> Renderer<T> getRenderer(Class<T> clazz) {
		try {
			List<ClassLoader> classLoaders = collectClassLoaders(clazz.getClassLoader());

			return getRenderer(clazz, classLoaders);
		}
		catch (ClassNotFoundException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> Renderer<T> getRenderer(Class<T> rendererType, Iterable<ClassLoader> classLoaders)
			throws ClassNotFoundException, NoSuchMethodException {

		for (ClassLoader classLoader : classLoaders) {
			Renderer<T> renderer = doGetRenderer(rendererType, classLoader);
			if (renderer != null) {
				return renderer;
			}
		}

		throw new ClassNotFoundException("Cannot find implementation for " + rendererType.getName());
	}

	@SuppressWarnings("unchecked")
	private static <T> Renderer<T> doGetRenderer(Class<T> clazz, ClassLoader classLoader) throws NoSuchMethodException {
		try {
			// TODO use annotation to resolve renderer name
			Class<?> implementation = (Class<?>) classLoader.loadClass(resolveName(clazz));
			Constructor<?> constructor = implementation.getDeclaredConstructor();
			constructor.setAccessible(true);

			return (Renderer<T>) constructor.newInstance();
		}
		catch (ClassNotFoundException e) {
			return (Renderer<T>) getRendererFromServiceLoader(clazz, classLoader);
		}
		catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static String resolveName(Class<?> c) {
		var a = c.getAnnotation(JStache.class);
		String cname;
		if (a != null && !":auto".equals(a.adapterName())) {
			cname = a.adapterName();
		}
		else {
			cname = c.getSimpleName() + IMPLEMENTATION_SUFFIX;
		}
		String packageName = c.getPackageName();
		String fqn = packageName + (packageName.isEmpty() ? "" : ".") + cname;
		return fqn;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> Renderer<?> getRendererFromServiceLoader(Class<T> clazz, ClassLoader classLoader) {
		ServiceLoader<Renderer> loader = ServiceLoader.load(Renderer.class, classLoader);

		for (Renderer renderer : loader) {
			if (renderer != null && renderer.supportsType(clazz)) {
				return renderer;
			}
		}

		return null;
	}

	private static List<ClassLoader> collectClassLoaders(ClassLoader classLoader) {
		List<ClassLoader> classLoaders = new ArrayList<>(3);
		classLoaders.add(classLoader);

		if (Thread.currentThread().getContextClassLoader() != null) {
			classLoaders.add(Thread.currentThread().getContextClassLoader());
		}

		classLoaders.add(Renderers.class.getClassLoader());

		return classLoaders;
	}

}