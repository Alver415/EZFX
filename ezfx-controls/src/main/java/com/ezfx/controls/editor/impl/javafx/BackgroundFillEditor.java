package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.BackgroundFillEditorSkin;
import javafx.beans.property.Property;
import javafx.scene.control.Skin;
import javafx.scene.layout.BackgroundFill;

public class BackgroundFillEditor extends ObjectEditor<BackgroundFill> {
	public BackgroundFillEditor(Property<BackgroundFill> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BackgroundFillEditorSkin(this);
	}

}
