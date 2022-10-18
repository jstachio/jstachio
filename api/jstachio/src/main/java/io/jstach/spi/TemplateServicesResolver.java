package io.jstach.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import io.jstach.RenderFunction;

enum TemplateServicesResolver implements TemplateServices {

    INSTANCE;

    private static class Holder {

        private static Holder INSTANCE = Holder.of();

        private final Iterable<TemplateServices> renderServices;

        private Holder(Iterable<TemplateServices> renderServices) {
            super();
            this.renderServices = renderServices;
        }

        private static Holder of() {
            Iterable<TemplateServices> it = ServiceLoader.load(TemplateServices.class);
            List<TemplateServices> svs = new ArrayList<>();
            it.forEach(svs::add);
            return new Holder(List.copyOf(svs));
        }
    }

    @Override
    public RenderFunction renderer(String template, Object context, RenderFunction previous) throws IOException {
        RenderFunction current = previous;
        for (var rs : Holder.INSTANCE.renderServices) {
            current = rs.renderer(template, context, current);
        }
        return current;
    }

    @Override
    public Formatter formatter(Formatter formatter) {
        Formatter current = formatter;
        for (var rs : Holder.INSTANCE.renderServices) {
            current = rs.formatter(current);
        }
        return current;
    }

}
