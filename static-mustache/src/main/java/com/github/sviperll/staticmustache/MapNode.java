package com.github.sviperll.staticmustache;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.Nullable;


/**
 * This interface is to allow you to simulate JSON node like trees
 * without being coupled to a particularly JSON lib.
 * 
 * It is not recommended you use this extension point as it generally avoids the type checking safty of this library.
 * 
 * It is mainly used for the spec test.
 * 
 * @author agentgt
 *
 */
public interface MapNode extends Iterable<MapNode> {

    public static @Nullable MapNode ofRoot(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof MapNode n) {
            return n;
        }
        return new RootMapNode(o);
        
    }
    
    default @Nullable MapNode ofChild(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        return new ChildMapNode(this, o);
    }
    
    default @Nullable MapNode get(String field) {
        /*
         * In theory we could make a special RenderingContext for MapNode
         * to go up the stack (generated code) but it would probably should look similar
         * to the following.
         */
        Object o = object();
        MapNode child = null;
        if (o instanceof Map<?,?> m) {
            child = ofChild(m.get(field));
        }
        if (child != null) {
            return child;
        }
        var parent = parent();
        if (parent != null && parent != this) {
            child = parent.get(field);
        }
        return child;
    }
    
    public Object object();
    
    default String renderString() {
        return String.valueOf(object());
    }
    
    default @Nullable MapNode parent() {
        return null;
    }
    
    @Override
    default Iterator<MapNode> iterator() {
        Object o = object();
        if (o instanceof Iterable<?> it) {
            return StreamSupport.stream(it.spliterator(), false).map(this::ofChild).iterator();
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
    
    public record RootMapNode(Object object) implements MapNode {
        @Override
        public String toString() {
            return renderString();
        }
    }
    
    public record ChildMapNode(MapNode parent, Object object) implements MapNode {
        @Override
        public String toString() {
            return renderString();
        }
    }
}
