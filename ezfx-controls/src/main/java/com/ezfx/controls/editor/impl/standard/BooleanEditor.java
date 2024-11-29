package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.CheckBoxSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Skin;

public class BooleanEditor extends ObjectEditor<Boolean> {

	public BooleanEditor() {
		this(new SimpleBooleanProperty(false));
	}

	public BooleanEditor(Property<Boolean> property) {
		super(property);
		getKnownValues().add(Boolean.TRUE);
		getKnownValues().add(Boolean.FALSE);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CheckBoxSkin(this);
	}

}
