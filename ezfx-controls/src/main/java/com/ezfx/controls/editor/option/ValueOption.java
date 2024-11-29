
package com.ezfx.controls.editor.option;

public abstract class ValueOption<T> extends Option<T> {

	public ValueOption(String name, Class<T> type) {
		super(name, type);
	}

	public abstract T getValue();
}