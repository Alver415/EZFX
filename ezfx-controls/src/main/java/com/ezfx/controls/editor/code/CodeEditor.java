package com.ezfx.controls.editor.code;

import com.ezfx.controls.editor.impl.standard.StringEditor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Skin;


public class CodeEditor extends StringEditor {

	public CodeEditor() {
		this(new SimpleStringProperty());
	}

	public CodeEditor(Property<String> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CodeEditorSkin(this);
	}

}