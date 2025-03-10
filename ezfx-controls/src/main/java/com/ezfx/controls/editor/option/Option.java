package com.ezfx.controls.editor.option;

import java.lang.reflect.Type;

public abstract class Option<T> {
	protected final String name;
	protected final Type type;

	public Option(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return name;
	}


}

