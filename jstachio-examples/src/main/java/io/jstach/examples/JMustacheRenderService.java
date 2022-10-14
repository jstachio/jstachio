package io.jstach.examples;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import io.jstach.spi.RenderService;
import io.jstach.text.RenderFunction;

public class JMustacheRenderService implements RenderService {

    
    private static final ThreadLocal<Boolean> enabled = ThreadLocal.withInitial(() -> false);
    
    public static void setEnabled(boolean f) {
        enabled.set(f);
    }

    @Override
    public RenderFunction renderer(String template, Object context, RenderFunction previous) throws IOException {
        if (!enabled.get())
            return previous;
        return (a) -> {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try (InputStream is = loader.getResourceAsStream(template);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                out.println("Using JMustache");
                Template t = Mustache.compiler().standardsMode(false).compile(br);
                String results = t.execute(context);
                a.append(results);
            }
        };

    }

}
