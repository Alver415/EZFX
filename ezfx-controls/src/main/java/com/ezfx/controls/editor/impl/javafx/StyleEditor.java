package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.code.CSSEditorSkin;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;


public class StyleEditor extends StringEditor {

	public StyleEditor(String value) {
		super(new SimpleObjectProperty<>(value));
	}

	public StyleEditor(Property<String> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CSSEditorSkin(this);
	}

}
