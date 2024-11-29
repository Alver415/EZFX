package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.BorderEditorSkin;
import javafx.beans.property.Property;
import javafx.scene.control.Skin;
import javafx.scene.layout.Border;

public class BorderEditor extends ObjectEditor<Border> {
	public BorderEditor(Property<Border> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BorderEditorSkin(this);
	}

}
