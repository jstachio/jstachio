package io.jstach.jstachio.spi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.spi.JStachioTemplateFinder.SimpleTemplateFinder;

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
public non-sealed interface JStachioTemplateFinder extends JStachioExtension {

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
	 * @throws Exception if any reflection or runtime error happens
	 * @throws NoSuchElementException if the template is not found and there were no other
	 * errors
	 * @throws NullPointerException if the modelType is null
	 */
	public TemplateInfo findTemplate(Class<?> modelType) throws Exception;

	/**
	 * Finds a template or null if no template is found. Should not throw an exception if
	 * a template is not found.
	 * @param modelType the models class (<em>the one annotated with {@link JStache} and
	 * not the Templates class</em>)
	 * @return <code>null</code> if the template is was not found or the template info
	 * which might be a {@link Template} if the generated template was found.
	 * @throws NullPointerException if the modelType is null
	 * @see #findTemplate(Class)
	 */
	default @Nullable TemplateInfo findOrNull(Class<?> modelType) {
		Objects.requireNonNull(modelType, "modelType");
		if (Templates.isIgnoredType(modelType)) {
			return null;
		}
		try {
			return findTemplate(modelType);
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * Determines if this template finder has a template for the model type (the class
	 * annotated by JStache).
	 * @param modelType the models class (<em>the one annotated with {@link JStache} and
	 * not the Templates class</em>)
	 * @return true if this finder has template for modelType
	 * @throws NullPointerException if the modelType is null
	 */
	default boolean supportsType(Class<?> modelType) {
		if (Templates.isIgnoredType(modelType)) {
			return false;
		}
		var t = findOrNull(modelType);
		if (t == null) {
			return false;
		}
		return true;
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
	 * <em>The returned finder will only call {@link #findTemplate(Class)} on the passed
	 * in delegate finder to resolve {@link #supportsType(Class)} and
	 * {@link #findOrNull(Class)}! </em>
	 * <p>
	 * While the finder does not provide any eviction the cache will not prevent garbage
	 * collection of the model classes.
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

	/**
	 * Creates a template finder from an iterable of templates. The returned finder will
	 * just loop through the templates and call {@link TemplateInfo#supportsType(Class)}.
	 * To avoid the looping cost wrap the return with
	 * {@link #cachedTemplateFinder(JStachioTemplateFinder)}.
	 * @param templates templates to be searched in order of the iterable
	 * @param order order hint see {@link #order()}.
	 * @return adapted template finder
	 */
	public static JStachioTemplateFinder of(Iterable<? extends TemplateInfo> templates, int order) {
		return new IterableTemplateFinder(templates, order);
	}

	/**
	 * Creates a composite template finder from a list. If the list only has a single
	 * element then it is returned without being wrapped.
	 * @param templateFinders list of template finders.
	 * @return templateFinder searching in order of {@link #order()} then the list order
	 */
	public static JStachioTemplateFinder of(List<? extends JStachioTemplateFinder> templateFinders) {
		return CompositeTemplateFinder.of(templateFinders);
	}

	/**
	 * An easier to implement template finder based on a sequence of templates.
	 *
	 * @author agentgt
	 *
	 */
	public interface SimpleTemplateFinder extends JStachioTemplateFinder {

		@Override
		default TemplateInfo findTemplate(Class<?> modelType) throws Exception {
			Objects.requireNonNull(modelType, "modelType");
			var t = findOrNull(modelType);
			if (t == null) {
				throw new TemplateNotFoundException(modelType);
			}
			return t;
		}

		@Override
		default boolean supportsType(Class<?> modelType) {
			Objects.requireNonNull(modelType, "modelType");
			var t = findOrNull(modelType);
			if (t == null) {
				return false;
			}
			return true;
		}

		@Override
		default @Nullable TemplateInfo findOrNull(Class<?> modelType) {
			var resolvedType = Templates.findJStache(modelType).getKey();
			for (var t : templates()) {
				if (t.supportsType(resolvedType)) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Sequence of templates used to find matching template from model.
		 * @return templates
		 */
		Iterable<? extends TemplateInfo> templates();

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
	public @Nullable TemplateInfo findOrNull(Class<?> modelType) {
		return Templates.findTemplateOrNull(modelType, config);
	}

	@Override
	public int order() {
		return Integer.MAX_VALUE;
	}

}

class TemplateNotFoundException extends NoSuchElementException {

	private static final long serialVersionUID = -4016359589653582060L;

	private final Class<?> modelType;

	protected TemplateNotFoundException(Class<?> modelType, @Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
		this.modelType = modelType;
	}

	public TemplateNotFoundException(Class<?> modelType) {
		this(modelType, errorMessage(modelType), (Throwable) null);
	}

	public TemplateNotFoundException(String message, Class<?> modelType) {
		this(modelType, message + " " + errorMessage(modelType), (Throwable) null);
	}

	protected static String errorMessage(Class<?> modelType) {
		return "Template not found for type: " + modelType;
	}

	public Class<?> modelType() {
		return modelType;
	}

}

final class IterableTemplateFinder implements SimpleTemplateFinder {

	private final Iterable<? extends TemplateInfo> templates;

	private final int order;

	public IterableTemplateFinder(Iterable<? extends TemplateInfo> templates, int order) {
		super();
		this.templates = templates;
		this.order = order;
	}

	@Override
	public int order() {
		return this.order;
	}

	@Override
	public Iterable<? extends TemplateInfo> templates() {
		return templates;
	}

}

final class ClassValueCacheTemplateFinder implements JStachioTemplateFinder {

	private final ClassValue<TemplateInfo> cache;

	private final JStachioTemplateFinder delegate;

	public ClassValueCacheTemplateFinder(JStachioTemplateFinder delegate) {
		super();
		this.delegate = delegate;
		this.cache = new ClassValue<>() {

			@Override
			protected @Nullable TemplateInfo computeValue(@Nullable Class<?> type) {
				try {
					return delegate.findTemplate(Objects.requireNonNull(type));
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
		Objects.requireNonNull(modelType);
		return Objects.requireNonNull(cache.get(modelType));
	}

	@Override
	public int order() {
		return delegate.order();
	}

}

final class CompositeTemplateFinder implements JStachioTemplateFinder {

	private final Iterable<? extends JStachioTemplateFinder> finders;

	private CompositeTemplateFinder(Iterable<? extends JStachioTemplateFinder> finders) {
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
			var t = f.findOrNull(modelType);
			if (t != null) {
				return t;
			}
		}
		throw new TemplateNotFoundException(modelType);
	}

	@Override
	public boolean supportsType(Class<?> modelType) {
		for (var f : finders) {
			var b = f.supportsType(modelType);
			if (b)
				return true;
		}
		return false;
	}

}
