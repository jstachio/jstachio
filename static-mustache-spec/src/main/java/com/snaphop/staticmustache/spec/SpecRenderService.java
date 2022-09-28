package com.snaphop.staticmustache.spec;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.kohsuke.MetaInfServices;

import com.github.sviperll.staticmustache.spi.Formatter;
import com.github.sviperll.staticmustache.spi.RenderService;

@MetaInfServices(RenderService.class)
public class SpecRenderService implements RenderService {

    @Override
    public Formatter formatter(String path, @Nullable Object context, Formatter previous) throws IOException {
        return MyFormatter.INSTANCE;
    }
    
    private enum MyFormatter implements Formatter {
        INSTANCE;

        @Override
        public boolean format(Appendable a, String path, @Nullable Object o) throws IOException {
            if (o != null) {
               a.append(String.valueOf(o));
               return true;
            }
            return false;
        }
    }
}
