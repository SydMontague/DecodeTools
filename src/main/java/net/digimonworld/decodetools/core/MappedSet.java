package net.digimonworld.decodetools.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class MappedSet<K, V> implements Set<V> {
    private Class<V> clazz;
    private Map<K, V> backingMap = new HashMap<>();
    private final Function<V, K> accessor;
    
    public MappedSet(Class<V> clazz, Function<V, K> accessor) {
        this.accessor = accessor;
        this.clazz = clazz;
    }

    public MappedSet(Class<V> clazz, Function<V, K> accessor, List<V> list) {
        this.accessor = accessor;
        this.clazz = clazz;
        
        list.forEach(this::add);
    }

    public V get(K key) {
        return backingMap.get(key);
    }
    
    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backingMap.containsValue(o);
    }

    @Override
    public Iterator<V> iterator() {
        return backingMap.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return backingMap.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return backingMap.values().toArray(a);
    }

    @Override
    public boolean add(V e) {
        backingMap.put(accessor.apply(e), e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return clazz.isInstance(o) && backingMap.remove(accessor.apply(clazz.cast(o))) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backingMap.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for(Object a : c)
            changed |= this.remove(a);
        
        return changed;
    }

    @Override
    @Deprecated
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        backingMap.clear();
    }
}
