package io.jstach.jstachio.spi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.Renderer;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;

//TODO renamed to Templates
class Renderers {

	private Renderers() {
	}

	public static <T> Template<T> getRenderer(Class<T> clazz) {
		try {
			List<ClassLoader> classLoaders = collectClassLoaders(clazz.getClassLoader());

			return (Template<T>) getRenderer(clazz, classLoaders);
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
			cname = c.getSimpleName() + Renderer.IMPLEMENTATION_SUFFIX;
		}
		String packageName = c.getPackageName();
		String fqn = packageName + (packageName.isEmpty() ? "" : ".") + cname;
		return fqn;
	}

	private static <T> Renderer<?> getRendererFromServiceLoader(Class<T> clazz, ClassLoader classLoader) {
		ServiceLoader<RendererProvider> loader = ServiceLoader.load(RendererProvider.class, classLoader);
		for (RendererProvider rp : loader) {
			for (var renderer : rp.provideRenderers()) {
				if (renderer instanceof TemplateInfo t && t.supportsType(clazz)) {
					return renderer;
				}
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