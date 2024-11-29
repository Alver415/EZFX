package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.ImagePatternEditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.paint.ImagePattern;

public class ImagePatternEditor extends ObjectEditor<ImagePattern> {

	public ImagePatternEditor() {
		super(new SimpleObjectProperty<>());
	}
	public ImagePatternEditor(Property<ImagePattern> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ImagePatternEditorSkin(this);
	}
}
