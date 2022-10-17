package io.jstach.spec.generator;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.kohsuke.MetaInfServices;

import io.jstach.spi.Appender;
import io.jstach.spi.Formatter;
import io.jstach.spi.Formatter.DownstreamFormatter;
import io.jstach.spi.RenderService;

@MetaInfServices(RenderService.class)
public class SpecRenderService implements RenderService {

    @Override
    public Formatter formatter(String path, @Nullable Object context, Formatter previous) {
        return MyFormatter.INSTANCE;
    }
    
    private enum MyFormatter implements DownstreamFormatter {
        INSTANCE;

        @Override
        public void format(Appender downstream, Appendable a, String path, Class<?> type, @Nullable Object o)
                throws IOException {
            if (o != null) {
                downstream.append(a, String.valueOf(o));
            }
        }
    }
}
