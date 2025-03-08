package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.scene.control.Skin;

public class ApplicationEditor extends ObjectEditor<Application> {
	public ApplicationEditor(Property<Application> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ApplicationEditorSkin(this);
	}

}
