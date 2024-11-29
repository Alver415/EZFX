package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.FontEditorSkin;
import javafx.beans.property.Property;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;

public class FontEditor extends ObjectEditor<Font> {
	public FontEditor(Property<Font> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new FontEditorSkin(this);
	}

}
