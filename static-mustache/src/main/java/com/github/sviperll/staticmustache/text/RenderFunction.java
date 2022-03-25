package com.github.sviperll.staticmustache.text;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public interface RenderFunction {
    
    public void render(Appendable a) throws IOException;
    
    @SuppressWarnings("null")
    default String renderString() {
        return append(new StringBuilder()).toString();
    }
    
    default StringBuilder append(StringBuilder sb) {
        try {
            render(sb);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return sb;
    }
    
    default <A extends Appendable> A append(A a) throws IOException {
        render(a);
        return a;
    }
    
    default RenderFunction withLayout(LayoutFunction lf) {
        return lf.withBody(this);
    }
    
    public static RenderFunction of(List<? extends RenderFunction> rfs) {
        return new Composite(rfs);
    }
    
    static class Composite implements RenderFunction {
        private final List<?  extends RenderFunction> functions;

        public Composite(List<? extends RenderFunction> functions) {
            super();
            this.functions = functions;
        }

        @Override
        public void render(Appendable a) throws IOException {
            for(var f : functions) {
                f.render(a);
            }
        }
        
    }

}
