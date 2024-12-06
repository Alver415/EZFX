package com.ezfx.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public interface Converters {
	Logger log = LoggerFactory.getLogger(Converters.class);

	Converter<String, Boolean> STRING_TO_BOOLEAN = build(Boolean::valueOf, Object::toString);
	Converter<String, Byte> STRING_TO_BYTE = build(Byte::valueOf, Object::toString);
	Converter<String, Integer> STRING_TO_INTEGER = build(Integer::valueOf, Object::toString);
	Converter<String, Long> STRING_TO_LONG = build(Long::valueOf, Object::toString);
	Converter<String, Double> STRING_TO_DOUBLE = build(Double::valueOf, Object::toString);
	Converter<String, Float> STRING_TO_FLOAT = build(Float::valueOf, Object::toString);

	Converter<String, byte[]> STRING_TO_BYTE_ARRAY = build(String::getBytes, String::new);

	Converter<Double, Float> DOUBLE_TO_FLOAT = build(Double::floatValue, Float::doubleValue);

	private static <A, B> Converter<A, B> build(Function<A, B> to, Function<B, A> from) {
		return Converter.of(wrap(to), wrap(from));

	}
	private static <T, R> Function<T, R> wrap(Function<T, R> function) {
		return t -> {
			try {
				return function.apply(t);
			} catch (Exception e) {
				log.trace(e.getMessage(), e);
				return null;
			}
		};
	}
}
