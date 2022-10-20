package io.jstach.context;

import java.util.Iterator;

public class ContextIterator<T> implements Iterator<T> {
    
    private final Iterator<T> iterator;

    private T item;
    private int index = -1;
    
    public ContextIterator(Iterator<T> iterator) {
        super();
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        var n = item = iterator.next();
        ++index;
        return  n;
    }
    
    public T current() {
        return item;
    }
    
    public boolean last() {
        return hasNext();
    }
}
