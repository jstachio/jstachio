package io.jstach.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import io.jstach.Formatter;
import io.jstach.RenderFunction;

enum JStachServicesResolver implements JStachServices {

    INSTANCE;

    private static class Holder {

        private static Holder INSTANCE = Holder.of();

        private final Iterable<JStachServices> services;

        private Holder(Iterable<JStachServices> services) {
            super();
            this.services = services;
        }

        private static Holder of() {
            Iterable<JStachServices> it = ServiceLoader.load(JStachServices.class);
            List<JStachServices> svs = new ArrayList<>();
            it.forEach(svs::add);
            return new Holder(List.copyOf(svs));
        }
    }

    @Override
    public RenderFunction renderer(String template, Object context, RenderFunction previous) throws IOException {
        RenderFunction current = previous;
        for (var rs : Holder.INSTANCE.services) {
            current = rs.renderer(template, context, current);
        }
        return current;
    }

    @Override
    public Formatter formatter(Formatter formatter) {
        Formatter current = formatter;
        for (var rs : Holder.INSTANCE.services) {
            current = rs.formatter(current);
        }
        return current;
    }

}
