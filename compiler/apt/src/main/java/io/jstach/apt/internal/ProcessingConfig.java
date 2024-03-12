package io.jstach.apt.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.NamedTemplate.FileTemplate;
import io.jstach.apt.internal.NamedTemplate.InlineTemplate;
import io.jstach.apt.prism.Prisms.Flag;

public interface ProcessingConfig extends LoggingSupport.MessagerLogging {

	public Set<Flag> flags();

	Charset charset();

	@Override
	default boolean isDebug() {
		return flags().contains(Flag.DEBUG);
	}

	@Override
	default void debug(CharSequence message) {
		outWriter().println("[JSTACHIO] " + message);

	}

	@Override
	default void error(CharSequence message, Throwable t) {
		printError(message);
		errorWriter().println("[JSTACHIO] " + message);
		t.printStackTrace(errorWriter());
	}

	default List<String> resourcesPaths() {
		return List.of("src/main/resources");
	}

	public boolean fallbackToFilesystem();

	PathConfig pathConfig();

	default boolean isGradleEnabled() {
		return isGradle();
	}

	public static boolean isGradle() {
		String cmd = System.getProperty("sun.java.command");
		if (cmd != null) {
			return cmd.trim().startsWith("org.gradle.launcher.daemon.bootstrap.GradleDaemon");
		}
		return false;
	}

	public static @Nullable String eclipseVersion() {
		return System.getProperty("eclipse.buildId");
	}

	public static boolean isEclipse() {
		String v = eclipseVersion();
		if (v != null && !v.isBlank()) {
			return true;
		}
		return false;
	}

	public record PathConfig(String prefix, String suffix, boolean prefixUnspecified, boolean suffixUnspecified) {

		public URI resolveTemplatePath(NamedTemplate namedTemplate) throws URISyntaxException {
			if (namedTemplate instanceof FileTemplate ft) {
				return resolveTemplatePath(new URI(ft.path()));
			}
			else if (namedTemplate instanceof InlineTemplate it) {
				return new URI(it.path());
			}
			else {
				throw new IllegalStateException();
			}
		}

		public URI resolveTemplatePath(NamedTemplate rootTemplate, FileTemplate childTemplate)
				throws URISyntaxException {
			if (rootTemplate == childTemplate) {
				return resolveTemplatePath(new URI(rootTemplate.path()));
			}
			URI uri = resolveFragmentURI(rootTemplate, new URI(childTemplate.path()));
			return resolveTemplatePath(uri);
		}

		public URI resolveTemplatePath(URI uri) throws URISyntaxException {
			String templatePath = uri.getPath();
			if (templatePath == null) {
				templatePath = "";
			}
			if (!templatePath.isBlank()) {
				templatePath = prefix() + templatePath + suffix();
			}
			return new URI(uri.getScheme(), uri.getHost(), templatePath, uri.getFragment());
		}

		static URI resolveFragmentURI(NamedTemplate rootTemplate, URI template) throws URISyntaxException {

			if (rootTemplate instanceof InlineTemplate) {
				return template;
			}
			var rootUri = new URI(rootTemplate.path());
			String fragment = template.getFragment();
			String path = template.getPath();
			path = path == null ? "" : path;

			if (fragment != null && (fragment.isBlank() || path == null)) {
				throw new URISyntaxException(template.toString(), "Fragment is blank");
			}
			else if (fragment != null && path.isBlank()) {
				if (rootUri.getFragment() != null) {
					throw new URISyntaxException(template.toString(), String.format(
							"Root template already has a fragment so \"%s\" is not valid URI", template.toString()));
				}
				return new URI(rootUri.getScheme(), rootUri.getHost(), rootUri.getPath(), fragment);
			}
			return template;
		}

	}

}
