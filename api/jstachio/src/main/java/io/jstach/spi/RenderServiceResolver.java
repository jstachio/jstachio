package io.jstach.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import io.jstach.RenderFunction;

enum RenderServiceResolver implements RenderService {

    INSTANCE;

    private static class Holder {

        private static Holder INSTANCE = Holder.of();

        private final Iterable<RenderService> renderServices;

        private Holder(Iterable<RenderService> renderServices) {
            super();
            this.renderServices = renderServices;
        }

        @SuppressWarnings("null")
        private static Holder of() {
            Iterable<RenderService> it = ServiceLoader.load(RenderService.class);
            List<RenderService> svs = new ArrayList<>();
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
    public Formatter formatter(String path, Object context, Formatter formatter) {
        Formatter current = formatter;
        for (var rs : Holder.INSTANCE.renderServices) {
            current = rs.formatter(path, context, current);
        }
        return current;
    }

}
