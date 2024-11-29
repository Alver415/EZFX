package com.ezfx.base.exception;

import java.util.function.Supplier;

@FunctionalInterface
public interface UncheckedSupplier<T> extends Supplier<T> {
	@Override
	default T get() {
		try {
			return tryGet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	T tryGet() throws Exception;
}