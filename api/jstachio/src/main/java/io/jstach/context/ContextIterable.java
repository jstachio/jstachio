package io.jstach.context;

import java.util.Iterator;


public class ContextIterable<T> implements Iterable<ContextIterable<T>> {

    private final Iterator<T> iterator;

    private T item;
    private int index = -1;
    private boolean last;

    public static <T> ContextIterable<T> of(Iterable<T> iterable) {
        return new ContextIterable<>(iterable.iterator());
    }

    public static <T> ContextIterable<T> of(T[] array) {
        return new ContextIterable<>(new Iterator<T>() {

            private int i;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public T next() {
                return array[i++];
            }
        });
    }

    public ContextIterable(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isFirst() {
        return index == 0;
    }

    public int getIndex() {
        return index;
    }

    public T object() {
        return item;
    }

    @Override
    public Iterator<ContextIterable<T>> iterator() {
        return new Iterator<ContextIterable<T>>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ContextIterable<T> next() {
                item = iterator.next();
                ++index;
                last = !hasNext();
                return ContextIterable.this;
            }
        };
    }
}