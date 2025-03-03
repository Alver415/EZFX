package com.ezfx.controls.editor;

import com.ezfx.base.utils.LRUCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LRUCacheTest {

	@Test
	public void test() throws Exception {

		int capacity = 4;
		LRUCache<String, Integer> cache = new LRUCache<>(capacity);

		for (int i = 0; i < 100; i++) {
			cache.put(String.valueOf(i), i);
		}

		assertEquals(capacity, cache.size());
	}
}
