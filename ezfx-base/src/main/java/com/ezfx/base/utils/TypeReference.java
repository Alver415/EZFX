package com.ezfx.base.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> {
	private final Type type;

	public TypeReference() {
		Type superclass = getClass().getGenericSuperclass();
		if (superclass instanceof Class<?>) {
			throw new RuntimeException("Missing type parameter.");
		}
		this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
	}

	public Type getType() {
		return this.type;
	}
	public Class<T> getTypeClass() {
		return (Class<T>) this.type;
	}
}