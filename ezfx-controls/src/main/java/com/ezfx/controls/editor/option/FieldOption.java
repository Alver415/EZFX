
package com.ezfx.controls.editor.option;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldOption<T> extends ValueOption<T> {

	private final Field field;

	public FieldOption(String name, Type type, Field field) {
		super(name, type);
		this.field = field;
	}

	public T getValue() {
		try {
			return (T) field.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}