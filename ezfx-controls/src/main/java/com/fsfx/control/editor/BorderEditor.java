package com.fsfx.control.editor;

import javafx.beans.property.Property;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderImage;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Paint;

import java.util.List;

public class BorderEditor extends EditorControl<Border> {
	public BorderEditor(Property<Border> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BorderEditorSkin(this);
	}

	public static class BorderEditorSkin extends SkinBase<BorderEditor> {

		private BorderEditorSkin(BorderEditor control) {
			super(control);

			Property<Border> borderProperty = control.getProperty();

		}
	}
}
