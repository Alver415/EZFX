
package com.ezfx.controls.editor.option;

import java.lang.reflect.Type;

public abstract class ValueOption<T> extends Option<T> {

	public ValueOption(String name, Type type) {
		super(name, type);
	}

	public abstract T getValue();
}