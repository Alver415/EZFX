package com.ezfx.spring.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface DelegatingMap<K, V> extends Map<K, V> {

	Map<K, V> getDelegateMap();

	@Override
	default int size() {
		return getDelegateMap().size();
	}

	@Override
	default boolean isEmpty() {
		return getDelegateMap().isEmpty();
	}

	@Override
	default boolean containsKey(Object key) {
		return getDelegateMap().containsKey(key);
	}

	@Override
	default boolean containsValue(Object value) {
		return getDelegateMap().containsValue(value);
	}

	@Override
	default V get(Object key) {
		return getDelegateMap().get(key);
	}

	@Override
	default V put(K key, V value) {
		return getDelegateMap().put(key, value);
	}

	@Override
	default V remove(Object key) {
		return getDelegateMap().remove(key);
	}

	@Override
	default void putAll(Map<? extends K, ? extends V> m) {
		getDelegateMap().putAll(m);
	}

	@Override
	default void clear() {
		getDelegateMap().clear();
	}

	@Override
	default Set<K> keySet() {
		return getDelegateMap().keySet();
	}

	@Override
	default Collection<V> values() {
		return getDelegateMap().values();
	}

	@Override
	default Set<Entry<K, V>> entrySet() {
		return getDelegateMap().entrySet();
	}

	@Override
	default V getOrDefault(Object key, V defaultValue) {
		return getDelegateMap().getOrDefault(key, defaultValue);
	}

	@Override
	default void forEach(BiConsumer<? super K, ? super V> action) {
		getDelegateMap().forEach(action);
	}

	@Override
	default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		getDelegateMap().replaceAll(function);
	}

	@Override
	default V putIfAbsent(K key, V value) {
		return getDelegateMap().putIfAbsent(key, value);
	}

	@Override
	default boolean remove(Object key, Object value) {
		return getDelegateMap().remove(key, value);
	}

	@Override
	default boolean replace(K key, V oldValue, V newValue) {
		return getDelegateMap().replace(key, oldValue, newValue);
	}

	@Override
	default V replace(K key, V value) {
		return getDelegateMap().replace(key, value);
	}

	@Override
	default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return getDelegateMap().computeIfAbsent(key, mappingFunction);
	}

	@Override
	default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return getDelegateMap().computeIfPresent(key, remappingFunction);
	}

	@Override
	default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return getDelegateMap().compute(key, remappingFunction);
	}

	@Override
	default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return getDelegateMap().merge(key, value, remappingFunction);
	}
}
