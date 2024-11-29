package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.InsetsEditorSkin;
import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.control.Skin;

public class InsetsEditor extends ObjectEditor<Insets> {
	public InsetsEditor(Property<Insets> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new InsetsEditorSkin(this);
	}

}
