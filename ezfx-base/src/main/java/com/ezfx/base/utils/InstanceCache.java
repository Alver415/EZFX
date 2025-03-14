package com.ezfx.base.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class InstanceCache {

	private final Map<Class<?>, Object> cache;

	public InstanceCache() {
		this.cache = new ConcurrentHashMap<>();
	}

	public <T> void put(Class<T> key, T value) {
		cache.put(key, value);
	}

	public <T> T get(Class<T> key) {
		return (T) cache.get(key);
	}

	public <T> T computeIfAbsent(Class<T> key, Supplier<T> function) {
		return (T) cache.computeIfAbsent(key, _ -> function.get());
	}
}
