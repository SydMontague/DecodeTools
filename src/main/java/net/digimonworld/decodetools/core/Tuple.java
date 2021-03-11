package net.digimonworld.decodetools.core;

/**
 * An immutable class of two generic values.
 *
 * @param <K> the first value, the "key"
 * @param <V> the second value, the "value"
 */
public class Tuple<K, V> {
    private final K key;
    private final V value;
    
    /**
     * Constructs a new Tuple with the given value.
     * 
     * @param key the first value
     * @param value the second value
     */
    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    /**
     * Returns the first value "key"
     * 
     * @return the first value
     */
    public K getKey() {
        return key;
    }
    
    /**
     * Returns the second value "value"
     * 
     * @return the second value
     */
    public V getValue() {
        return value;
    }
    
    public static <K, V> Tuple<K, V> of(K key, V value) {
        return new Tuple<>(key, value);
    }
}