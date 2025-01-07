package com.ezfx.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.function.Function;

public interface Converters {
	Logger log = LoggerFactory.getLogger(Converters.class);

	Converter<String, Boolean> STRING_TO_BOOLEAN = create(Boolean::valueOf, Object::toString);
	Converter<String, Byte> STRING_TO_BYTE = create(Byte::valueOf, Object::toString);
	Converter<String, Short> STRING_TO_SHORT = create(Short::valueOf, Object::toString);
	Converter<String, Integer> STRING_TO_INTEGER = create(Integer::valueOf, Object::toString);
	Converter<String, Long> STRING_TO_LONG = create(Long::valueOf, Object::toString);
	Converter<String, Float> STRING_TO_FLOAT = create(Float::valueOf, Object::toString);
	Converter<String, Double> STRING_TO_DOUBLE = create(Double::valueOf, Object::toString);
	Converter<String, Character> STRING_TO_CHARACTER = create(string -> string.charAt(0), Object::toString);

	Converter<String, byte[]> STRING_TO_BYTE_ARRAY = create(String::getBytes, String::new);
	Converter<String, ByteBuffer> STRING_TO_BYTE_BUFFER = create(s -> ByteBuffer.wrap(s.getBytes()), b -> new String(b.array()));

	Converter<Double, Float> DOUBLE_TO_FLOAT = create(Double::floatValue, Float::doubleValue);

	private static <A, B> Converter<A, B> create(Function<A, B> to, Function<B, A> from) {
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
