package com.ezfx.base.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Memoizer<T, U> {

	private final Map<T, U> cache = new ConcurrentHashMap<>();

	private Memoizer() {}

	private Function<T, U> doMemoize(final Function<T, U> function) {
		return arg -> cache.computeIfAbsent(arg, function);
	}

	public static <T, U> Function<T, U> memoize(final Function<T, U> function) {
		return new Memoizer<T, U>().doMemoize(function);
	}
}