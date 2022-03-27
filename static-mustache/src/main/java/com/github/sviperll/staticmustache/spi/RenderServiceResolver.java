package com.github.sviperll.staticmustache.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

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
    public boolean render(String template, Object context, Appendable a) throws IOException {
        for (var rs : Holder.INSTANCE.renderServices) {
            var stop = rs.render(template, context, a);
            if (stop) {
                return true;
            }
        }
        return false;
    }
    
    

}
