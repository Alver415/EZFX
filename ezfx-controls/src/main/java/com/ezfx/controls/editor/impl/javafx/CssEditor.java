package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.CssEditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.Styleable;
import javafx.scene.control.Skin;

public class CssEditor extends ObjectEditor<Styleable> {

	public CssEditor(Styleable value) {
		super(new SimpleObjectProperty<>(value));
	}

	public CssEditor(Property<Styleable> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CssEditorSkin(this);
	}

}
