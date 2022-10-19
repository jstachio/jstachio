package io.jstach.examples;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import io.jstach.RenderFunction;
import io.jstach.TemplateInfo;
import io.jstach.spi.JStachServices;

public class JMustacheRenderService implements JStachServices {

    private static final ThreadLocal<Boolean> enabled = ThreadLocal.withInitial(() -> false);

    public static void setEnabled(boolean f) {
        enabled.set(f);
    }

    @Override
    public RenderFunction renderer(TemplateInfo template, Object context, RenderFunction previous) throws IOException {
        if (!enabled.get())
            return previous;
        return (a) -> {
            out.println("Using JMustache");
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            switch (template.templateSource()) {
            case STRING -> {
                Template t = Mustache.compiler().standardsMode(false).compile(template.templateString());
                String results = t.execute(context);
                a.append(results);
            }
            case RESOURCE -> {
                try (InputStream is = loader.getResourceAsStream(template.templatePath());
                        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    Template t = Mustache.compiler().standardsMode(false).compile(br);
                    String results = t.execute(context);
                    a.append(results);
                }
            }
            }
        };

    }

}
