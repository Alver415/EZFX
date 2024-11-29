package com.ezfx.controls.editor.option;

public abstract class Option<T> {
	protected final String name;
	protected final Class<T> type;

	public Option(String name, Class<T> type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public String toString() {
		return name;
	}


}

