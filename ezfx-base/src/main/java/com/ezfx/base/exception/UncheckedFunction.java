package com.ezfx.base.exception;

import java.util.function.Function;

@FunctionalInterface
public interface UncheckedFunction<T, R> extends Function<T, R> {

	@Override
	default R apply(T value) {
		try {
			return tryApply(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	R tryApply(T value) throws Exception;
}