package io.jstach.jstachio.spi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;

/**
 * Finds templates based on the model type (class).
 * <p>
 * The default {@link JStachio} uses a combination of relection and the ServiceLoader to
 * find templates.
 * <p>
 * Other implementations may want to use their DI framework like Spring or CDI to find
 * templates.
 *
 * @author agentgt
 *
 */
public interface JStachioTemplateFinder {

	/**
	 * Finds a {@link Template} if possible otherwise possibly falling back to a
	 * {@link TemplateInfo} based on annotation metadata or some other mechanism.
	 *
	 * @apiNote Callers can do an <code>instanceof Template t</code> to see if a generated
	 * template was returned instead of the fallback {@link TemplateInfo} metadata.
	 * @param modelType the models class (<em>the one annotated with {@link JStache} and
	 * not the Templates class</em>)
	 * @return the template info which might be a {@link Template} if the generated
	 * template was found.
	 * @throws Exception if any reflection error happes or the template is not found
	 */
	public TemplateInfo findTemplate(Class<?> modelType) throws Exception;

	/**
	 * Determines if this template finder has a template for the model type (the class
	 * annotated by JStache).
	 * @param modelType the models class (<em>the one annotated with {@link JStache} and
	 * not the Templates class</em>)
	 * @return true if this finder has template for modelType
	 */
	default boolean supportsType(Class<?> modelType) {
		try {
			findTemplate(modelType);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Hint on order of template finders. The found {@link JStachioTemplateFinder}s are
	 * sorted naturally (lower number comes first) based on the returned number. Thus a
	 * template finder with a lower order number that {@link #supportsType(Class)} the
	 * model class will be used.
	 * @return default returns zero
	 */
	default int order() {
		return 0;
	}

	/**
	 * The default template finder that uses reflection and or the ServiceLoader.
	 * <p>
	 * <em>This implementation performs no caching. If you would like caching call
	 * {@link #cachedTemplateFinder(JStachioTemplateFinder)} on the returned finder.</em>
	 * @param config used to help find templates as well as logging.
	 * @return default template finder.
	 */
	public static JStachioTemplateFinder defaultTemplateFinder(JStachioConfig config) {
		return new DefaultTemplateFinder(config);
	}

	/**
	 * Decorates a template finder with a cache using {@link ClassValue} with the
	 * modelType as the key.
	 * <p>
	 * <em>While the finder does not provide any eviction the cache will not prevent
	 * garbage collection of the model classes.</em>
	 * @param finder to be decorated unless the finder is already decorated thus it is a
	 * noop to repeateadly call this method on already cached template finder.
	 * @return caching template finder
	 */
	public static JStachioTemplateFinder cachedTemplateFinder(JStachioTemplateFinder finder) {
		if (finder instanceof ClassValueCacheTemplateFinder) {
			return finder;
		}
		return new ClassValueCacheTemplateFinder(finder);
	}

}

final class DefaultTemplateFinder implements JStachioTemplateFinder {

	private final JStachioConfig config;

	DefaultTemplateFinder(JStachioConfig config) {
		super();
		this.config = config;
	}

	@Override
	public TemplateInfo findTemplate(Class<?> modelType) throws Exception {
		return Templates.findTemplate(modelType, config);
	}

	@Override
	public int order() {
		return Integer.MAX_VALUE;
	}

}

final class ClassValueCacheTemplateFinder implements JStachioTemplateFinder {

	private final ClassValue<TemplateInfo> cache;

	private final JStachioTemplateFinder delegate;

	public ClassValueCacheTemplateFinder(JStachioTemplateFinder delegate) {
		super();
		this.delegate = delegate;
		this.cache = new ClassValue<TemplateInfo>() {

			@Override
			protected @Nullable TemplateInfo computeValue(@Nullable Class<?> type) {
				try {
					return delegate.findTemplate(type);
				}
				catch (Exception e) {
					Templates.sneakyThrow(e);
					throw new RuntimeException();
				}
			}
		};
	}

	@Override
	public TemplateInfo findTemplate(Class<?> modelType) throws Exception {
		return cache.get(modelType);
	}

	@Override
	public int order() {
		return delegate.order();
	}

}

final class CompositeTemplateFinder implements JStachioTemplateFinder {

	private final List<JStachioTemplateFinder> finders;

	private CompositeTemplateFinder(List<JStachioTemplateFinder> finders) {
		super();
		this.finders = finders;
	}

	public static JStachioTemplateFinder of(List<? extends JStachioTemplateFinder> finders) {
		if (finders.size() == 1) {
			return finders.get(0);
		}
		ArrayList<JStachioTemplateFinder> sorted = new ArrayList<>();
		sorted.addAll(finders);
		sorted.sort(Comparator.comparingInt(JStachioTemplateFinder::order));
		return new CompositeTemplateFinder(List.copyOf(sorted));
	}

	@Override
	public TemplateInfo findTemplate(Class<?> modelType) throws Exception {
		for (var f : finders) {
			if (f.supportsType(modelType)) {
				return f.findTemplate(modelType);
			}
		}
		throw new RuntimeException("Template not found for type: " + modelType);
	}

}
