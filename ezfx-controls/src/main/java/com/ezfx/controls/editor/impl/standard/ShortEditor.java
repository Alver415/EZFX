package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.LongFieldSkin;
import com.ezfx.controls.editor.skin.ShortFieldSkin;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

public class ShortEditor extends ObjectEditor<Short> {

	public ShortEditor() {
		this(new SimpleObjectProperty<>());
	}

	public ShortEditor(Property<Short> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ShortFieldSkin(this);
	}

}
