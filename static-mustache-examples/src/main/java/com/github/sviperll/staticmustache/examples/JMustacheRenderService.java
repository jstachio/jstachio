package com.github.sviperll.staticmustache.examples;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.github.sviperll.staticmustache.spi.RenderService;
import com.github.sviperll.staticmustache.text.RenderFunction;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

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
                Template t = Mustache.compiler().standardsMode(true).compile(br);
                String results = t.execute(context);
                a.append(results);
            }
        };

    }

}
