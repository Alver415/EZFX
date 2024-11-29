package com.ezfx.base.exception;

import java.util.function.Consumer;

@FunctionalInterface
public interface UncheckedConsumer<T> extends Consumer<T> {

	@Override
	default void accept(T value) {
		try {
			tryAccept(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void tryAccept(T value) throws Exception;
}