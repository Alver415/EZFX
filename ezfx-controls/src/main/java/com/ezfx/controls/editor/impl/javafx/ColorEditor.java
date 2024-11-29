package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.ColorEditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

public class ColorEditor extends ObjectEditor<Color> {

	public ColorEditor() {
		super(new SimpleObjectProperty<>());
	}
	public ColorEditor(Property<Color> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ColorEditorSkin(this);
	}
}
