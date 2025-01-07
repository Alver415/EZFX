package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.EditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

//TODO: Implement Skin
public class CharacterEditor extends ObjectEditor<Character> {

	public CharacterEditor() {
		this(new SimpleObjectProperty<>());
	}

	public CharacterEditor(Property<Character> property) {
		super(property);
	}
}
