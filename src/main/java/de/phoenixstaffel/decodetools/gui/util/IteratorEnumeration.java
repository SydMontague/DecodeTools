package de.phoenixstaffel.decodetools.gui.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Wraps an {@link Iterator} in an {@link Enumeration}, for APIs that only support the latter.
 */
public class IteratorEnumeration<E> implements Enumeration<E> {
    private Iterator<E> iter;

    /**
     * Creates a new instance with a given Iterator.
     */
    public IteratorEnumeration(Iterator<E> iter) {
        this.iter = iter;
    }
    
    @Override
    public boolean hasMoreElements() {
        return iter.hasNext();
    }

    @Override
    public E nextElement() {
        return iter.next();
    }
}