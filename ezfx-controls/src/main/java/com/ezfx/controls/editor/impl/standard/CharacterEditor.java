package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

//TODO: Implement Skin
public class CharacterEditor extends ObjectEditor<Character> {

	public CharacterEditor() {
		this(new SimpleObjectProperty<>());
	}

	public CharacterEditor(Property<Character> property) {
		super(property);
	}
}
