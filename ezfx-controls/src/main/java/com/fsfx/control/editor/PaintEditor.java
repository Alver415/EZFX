package com.fsfx.control.editor;

import javafx.beans.property.Property;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Paint;

import java.util.function.Function;

public class PaintEditor extends EditorControl<Paint> {
	public PaintEditor(Property<Paint> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new PaintEditorSkin(this);
	}

	public static class PaintEditorSkin extends SkinBase<PaintEditor> {

		private PaintEditorSkin(PaintEditor control) {
			super(control);
			control.propertyProperty().flatMap(Function.identity()).subscribe(paint -> {
			});

			getChildren().setAll();
		}
	}
}
