package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

//TODO: Implement Skin
public class ByteEditor extends ObjectEditor<Byte> {

	public ByteEditor() {
		this(new SimpleObjectProperty<>());
	}

	public ByteEditor(Property<Byte> property) {
		super(property);
	}
}
