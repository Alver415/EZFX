package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.PaintEditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.paint.Paint;

public class PaintEditor extends ObjectEditor<Paint> {

	public PaintEditor() {
		super(new SimpleObjectProperty<>());
	}

	public PaintEditor(Property<Paint> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new PaintEditorSkin(this);
	}


}
