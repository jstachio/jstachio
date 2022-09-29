package com.github.sviperll.staticmustache;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.Nullable;


public interface MapNode extends Iterable<MapNode> {

    public static @Nullable MapNode ofNullable(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof MapNode n) {
            return n;
        }
        return new DefaultMapNode(o);
        
    }
    
    default @Nullable MapNode get(String field) {
        Object o = object();
        if (o instanceof Map<?,?> m) {
            return ofNullable(m.get(field));
        }
        return null;
    }
    
    public Object object();
    
    default String renderString() {
        return String.valueOf(object());
    }
    
    @Override
    default Iterator<MapNode> iterator() {
        Object o = object();
        if (o instanceof Iterable<?> it) {
            return StreamSupport.stream(it.spliterator(), false).map(MapNode::ofNullable).iterator();
        }
        else if (isFalsey()) {
            return Collections.emptyIterator();
        }
        return Collections.singletonList(this).iterator();
    }
    
    default boolean isFalsey() {
        Object o = object();
        return Boolean.FALSE.equals(o);
    }
    
    public record DefaultMapNode(Object object) implements MapNode {
        @Override
        public String toString() {
            return renderString();
        }
    }
}
