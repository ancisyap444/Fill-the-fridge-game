package com.fillthefridge.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Registry<K, V> {
    private final Map<K, V> entries;

    public Registry() {
        this.entries = new LinkedHashMap<>();
    }

    public void register(K key, V value) {
        entries.put(key, value);
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(entries.get(key));
    }

    public V getOrDefault(K key, V fallback) {
        return entries.getOrDefault(key, fallback);
    }

    public boolean containsKey(K key) {
        return entries.containsKey(key);
    }

    public Set<K> keys() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    public Map<K, V> getAll() {
        return Collections.unmodifiableMap(entries);
    }

    public void clear() {
        entries.clear();
    }
}
