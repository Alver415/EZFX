package com.ezfx.base.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
	private final int capacity;

	public LRUCache() {
		this(-1);
	}
	public LRUCache(int capacity) {
		super(4, 0.75f, true);
		this.capacity = capacity;
	}

	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		if (capacity < 0) return false;
		return size() > capacity;
	}
}