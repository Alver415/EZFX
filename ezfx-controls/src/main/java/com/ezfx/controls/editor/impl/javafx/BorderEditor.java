package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.Property;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.Border;

public class BorderEditor extends ObjectEditor<Border> {
	public BorderEditor(Property<Border> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkinBase<BorderEditor, Border> {

		public DefaultSkin(BorderEditor control) {
			super(control);

			getChildren().setAll(new Label("NOT YET IMPLEMENTED"));
		}
	}
}
