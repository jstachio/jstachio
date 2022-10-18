package io.jstach.context;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
public interface ContextNode extends Iterable<ContextNode> {

    public static @Nullable ContextNode ofRoot(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof ContextNode n) {
            return n;
        }
        return new RootContextNode(o);
        
    }
    
    default @Nullable ContextNode ofChild(String name, @Nullable Object o) {
        if (o == null) {
            return null;
        }
        if ( o instanceof ContextNode) {
            throw new IllegalArgumentException("Cannot wrap ContextNode around another ContextNode");
        }
        return new NamedContextNode(this, o, name);
    }
    
    default @Nullable ContextNode ofChild(int index, @Nullable Object o) {
        if (o == null) {
            return null;
        }
        if ( o instanceof ContextNode) {
            throw new IllegalArgumentException("Cannot wrap ContextNode around another ContextNode");
        }
        return new IndexedContextNode(this, o, index);
    }
    
    /**
     * Gets a field from java.util.Map if ContextNode is wrapping one.
     * This is direct access and does not check the parents.
     * 
     * Just like java.util.Map null will be returned if no field is found.
     * 
     * @param field
     * @return a child node. maybe null.
     */
    default @Nullable ContextNode get(String field) {
        Object o = object();
        ContextNode child = null;
        if (o instanceof Map<?,?> m) {
            child = ofChild(field,m.get(field));
        }
        return child;
    }
    
    /**
     * Will search up the tree for a field starting at this nodes children first.
     * @param field
     * @return null if not found
     */
    default @Nullable ContextNode find(String field) {
        /*
         * In theory we could make a special RenderingContext for ContextNode
         * to go up the stack (generated code) but it would probably look similar
         * to the following.
         */
        ContextNode child = get(field);
        if (child != null) {
            return child;
        }
        var parent = parent();
        if (parent != null && parent != this) {
            child = parent.find(field);
            if (child != null) {
                child = ofChild(field, child.object());
            }
        }
        return child;
    }
    
    public Object object();
    
    default String renderString() {
        return String.valueOf(object());
    }
    
    default @Nullable ContextNode parent() {
        return null;
    }
    
    @Override
    default Iterator<ContextNode> iterator() {
        Object o = object();
        if (o instanceof Iterable<?> it) {
            AtomicInteger index = new AtomicInteger();
            return StreamSupport.stream(it.spliterator(), false)
                    .map( i -> this.ofChild(index.getAndIncrement(),  i)).iterator();
        }
        else if (o == null || Boolean.FALSE.equals(o)) {
            return Collections.emptyIterator();
        }
        return Collections.singletonList(this).iterator();
    }
    
    static boolean isFalsey(@Nullable Object context) {
        if (context == null) {
            return true;
        }
        if (Boolean.FALSE.equals(context)) {
            return true;
        }
        if (context instanceof Collection<?> c) {
            return c.isEmpty();
        }
        if (context instanceof Iterable<?> it) {
            return ! it.iterator().hasNext();
        }
        if (context.getClass().isArray() && Array.getLength(context) == 0) {
            return false;
        }
        return false;
    }
    
}

record RootContextNode(Object object) implements ContextNode {
    @Override
    public String toString() {
        return renderString();
    }
}

record NamedContextNode(ContextNode parent, Object object, String name) implements ContextNode {
    @Override
    public String toString() {
        return renderString();
    }
}

record IndexedContextNode(ContextNode parent, Object object, int index) implements ContextNode {
    @Override
    public String toString() {
        return renderString();
    }
}
