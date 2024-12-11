package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.code.CSSEditorSkin;
import com.ezfx.controls.editor.code.JavaEditorSkin;
import com.ezfx.controls.editor.code.XMLEditorSkin;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.editor.skin.FontEditorSkin;
import com.ezfx.controls.editor.skin.StyleEditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.Style;
import javafx.css.Styleable;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;


public class StyleEditor extends StringEditor {

	public StyleEditor(String value) {
		super(new SimpleObjectProperty<>(value));
	}

	public StyleEditor(Property<String> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CSSEditorSkin(this);
	}

}
