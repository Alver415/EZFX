package com.ezfx.controls.editor.option;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorBase;

import java.lang.reflect.Type;

public abstract class BuilderOption<T> extends Option<T> {

	public BuilderOption(String name, Type type) {
		super(name, type);
	}

	public abstract Editor<T> buildEditor();
}
