package com.ezfx.controls.editor.option;

import com.ezfx.controls.editor.Editor;

public abstract class BuilderOption<T> extends Option<T> {

	public BuilderOption(String name, Class<T> type) {
		super(name, type);
	}

	public abstract Editor<T> buildEditor();
}
