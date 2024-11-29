package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.BackgroundEditorSkin;
import javafx.beans.property.Property;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;

public class BackgroundEditor extends ObjectEditor<Background> {
	public BackgroundEditor(Property<Background> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BackgroundEditorSkin(this);
	}
}
