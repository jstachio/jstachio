package io.jstach.spec.generator;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.kohsuke.MetaInfServices;

import io.jstach.Appender;
import io.jstach.spi.Formatter;
import io.jstach.spi.TemplateServices;

@MetaInfServices(TemplateServices.class)
public class SpecRenderService implements TemplateServices {

    @Override
    public Formatter formatter(Formatter previous) {
        return MyFormatter.INSTANCE;
    }
    
    private enum MyFormatter implements Formatter {
        INSTANCE;
        
        @Override
        public <A extends Appendable, APPENDER extends Appender<A>> 
        void format(APPENDER downstream, A a, String path,
                Class<?> c, @Nullable Object o) throws IOException {
            if (o != null) {
                downstream.append(a, String.valueOf(o));
            }
        }
    }
}
