package com.ezfx.base.utils;

public interface Converters {

	Converter<String, Boolean> STRING_TO_BOOLEAN = Converter.of(Boolean::valueOf, Object::toString);
	Converter<String, Byte> STRING_TO_BYTE = Converter.of(Byte::valueOf, Object::toString);
	Converter<String, Integer> STRING_TO_INTEGER = Converter.of(Integer::valueOf, Object::toString);
	Converter<String, Long> STRING_TO_LONG = Converter.of(Long::valueOf, Object::toString);
	Converter<String, Double> STRING_TO_DOUBLE = Converter.of(Double::valueOf, Object::toString);
	Converter<String, Float> STRING_TO_FLOAT = Converter.of(Float::valueOf, Object::toString);

	Converter<String, byte[]> STRING_TO_BYTE_ARRAY = Converter.of(String::getBytes, String::new);

	Converter<Double, Float> DOUBLE_TO_FLOAT = Converter.of(Double::floatValue, Float::doubleValue);
}
