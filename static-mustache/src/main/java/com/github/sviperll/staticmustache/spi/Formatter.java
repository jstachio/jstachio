package com.github.sviperll.staticmustache.spi;

import java.io.IOException;

import org.jspecify.nullness.Nullable;

public interface Formatter {

    public boolean format(Appendable a, String path, @Nullable Object o) throws IOException;
    
    default boolean format(Appendable a, String path, Class<?> c, @Nullable Object o) throws IOException {
        return format(a, path, o);
    }


}
