
package com.ezfx.controls.editor.option;

import java.lang.reflect.Field;

public class FieldOption<T> extends ValueOption<T> {

	private final Field field;

	public FieldOption(String name, Class<T> type, Field field) {
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