package com.github.sviperll.staticmustache.examples;

import static java.lang.System.out;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.github.sviperll.staticmustache.spi.RenderService;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public class JMustacheRenderService implements RenderService {

    
    private static final ThreadLocal<Boolean> enabled = ThreadLocal.withInitial(() -> false);
    
    public static void setEnabled(boolean f) {
        enabled.set(f);
    }

    @Override
    public boolean render(String template, Object context, Appendable a) throws IOException {
        if (! enabled.get())
            return false;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = loader.getResourceAsStream(template);
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            out.println("Using JMustache");
            Template t = Mustache.compiler().standardsMode(true).compile(br);
            String results = t.execute(context);
            a.append(results);
            return true;
        }

    }

}
