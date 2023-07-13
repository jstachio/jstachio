package io.jstach.opt.dropwizard;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

import org.eclipse.jdt.annotation.Nullable;

import io.dropwizard.views.common.View;
import io.dropwizard.views.common.ViewRenderException;
import io.dropwizard.views.common.ViewRenderer;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output.EncodedOutput;
import io.jstach.jstachio.spi.JStachioConfig;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.jstachio.spi.JStachioFactory;

/**
 *
 * Dropwizard view support. Doing the below will automatically pick up JStachio
 * <pre><code class="language-java">
 * bootstrap.addBundle(new ViewBundle&lt;&gt;());
 * </code> </pre> Or alternatively you can pass it directly:
 * <pre><code class="language-java">
 * JStachio jstachio = ...; // See JStachioFactory
 * bootstrap.addBundle(new ViewBundle&lt;&gt;(new JStachioViewRenderer(jstachio)));
 * </code> </pre>
 *
 * @author agentgt
 */
@SuppressWarnings("exports")
public class JStachioViewRenderer implements ViewRenderer {

	private @Nullable JStachio jstachio = null;

	/**
	 * ServiceLoader will call this
	 */
	public JStachioViewRenderer() {
		// Empty on purpose (placate sonar)
	}

	/**
	 * Programmatically create the renderer with the given jstachio.
	 * @param jstachio a jstachio instance.
	 * @see JStachioFactory
	 */
	public JStachioViewRenderer(JStachio jstachio) {
		this.jstachio = jstachio;
	}

	@Override
	public boolean isRenderable(View view) {
		return view instanceof JStachioView;
	}

	@Override
	public void render(View view, Locale locale, OutputStream output) throws IOException {
		if (view instanceof JStachioView jv) {
			try (var out = EncodedOutput.of(output, jv.charset())) {
				jstachio().write(jv.model(), out);
			}
		}
		else {
			throw new ViewRenderException("Not a JStachioView: " + view);
		}
	}

	/**
	 * Internal getter for the jstachio backing this renderer
	 * @return not null
	 * @throws NullPointerException if jstachio has not been set yet.
	 */
	protected JStachio jstachio() {
		var j = jstachio;
		if (j == null) {
			throw new NullPointerException("JStachio has not been set!");
		}
		return j;
	}

	@Override
	public void configure(Map<String, String> options) {
		if (jstachio != null) {
			return;
		}
		if (options.isEmpty()) {
			jstachio = JStachio.of();
		}
		else {
			Map<String, String> resolved = new LinkedHashMap<>();
			for (var e : options.entrySet()) {
				if (e.getKey() != null && e.getValue() != null) {
					resolved.put(getConfigurationKey() + "." + e.getKey(), e.getValue());
				}
			}
			resolved = Map.copyOf(resolved);
			JStachioConfig dropWizardConfig = resolved::get;
			/*
			 * Hmm I wonder if we should add system properties here
			 */
			var j = jstachio = JStachioFactory.builder() //
					.add(dropWizardConfig) //
					.add(ServiceLoader.load(JStachioExtension.class)) //
					.build();

			JStachio.setStatic(() -> j);
		}
	}

	@Override
	public final String getConfigurationKey() {
		return "jstachio";
	}

}
