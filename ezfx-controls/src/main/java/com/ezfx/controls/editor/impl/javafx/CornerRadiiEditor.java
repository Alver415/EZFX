package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.CornerRadiiEditorSkin;
import javafx.beans.property.Property;
import javafx.scene.control.Skin;
import javafx.scene.layout.CornerRadii;

public class CornerRadiiEditor extends ObjectEditor<CornerRadii> {
	public CornerRadiiEditor(Property<CornerRadii> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CornerRadiiEditorSkin(this);
	}


}
