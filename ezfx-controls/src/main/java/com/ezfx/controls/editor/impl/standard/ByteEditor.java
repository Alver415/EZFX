package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.EditorSkin;
import com.ezfx.controls.editor.skin.TextFieldSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Skin;

//TODO: Implement Skin
public class ByteEditor extends ObjectEditor<Byte> {

	public ByteEditor() {
		this(new SimpleObjectProperty<>());
	}

	public ByteEditor(Property<Byte> property) {
		super(property);
	}
}
