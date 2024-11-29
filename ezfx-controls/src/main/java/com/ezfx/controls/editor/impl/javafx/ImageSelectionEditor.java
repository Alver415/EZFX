package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.ImageSelectionSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;

public class ImageSelectionEditor extends ObjectEditor<Image> {

	public ImageSelectionEditor() {
		super(new SimpleObjectProperty<>());
	}

	public ImageSelectionEditor(Property<Image> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ImageSelectionSkin(this);
	}

}
