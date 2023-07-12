package io.jstach.opt.dropwizard;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import org.kohsuke.MetaInfServices;

import io.dropwizard.views.common.View;
import io.dropwizard.views.common.ViewRenderException;
import io.dropwizard.views.common.ViewRenderer;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output.EncodedOutput;

/**
 *
 * Dropwizard view support
 *
 * @author agentgt
 */
@SuppressWarnings("exports")
@MetaInfServices(ViewRenderer.class)
public class JStachioViewRenderer implements ViewRenderer {

	/**
	 * ServiceLoader will call this
	 */
	public JStachioViewRenderer() {
	}

	@Override
	public boolean isRenderable(View view) {
		return view instanceof JStachioView;
	}

	@Override
	public void render(View view, Locale locale, OutputStream output) throws IOException {
		if (view instanceof JStachioView jv) {
			try (var out = EncodedOutput.of(output, jv.charset())) {
				JStachio.of().write(jv.model(), out);
			}
		}
		else {
			throw new ViewRenderException("Not a JStachioView: " + view);
		}
	}

	@Override
	public void configure(Map<String, String> options) {

	}

	@Override
	public String getConfigurationKey() {
		return "jstachio";
	}

}
