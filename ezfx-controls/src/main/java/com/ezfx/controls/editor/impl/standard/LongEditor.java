package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.IntegerFieldSkin;
import com.ezfx.controls.editor.skin.LongFieldSkin;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.scene.control.Skin;

public class LongEditor extends ObjectEditor<Long> {

	public LongEditor(LongProperty property) {
		this(property.asObject());
	}

	public LongEditor(Property<Long> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new LongFieldSkin(this);
	}

}
