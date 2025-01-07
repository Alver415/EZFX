package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.FontEditorSkin;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;

public class ApplicationEditor extends ObjectEditor<Application> {
	public ApplicationEditor(Property<Application> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ApplicationEditorSkin(this);
	}

}
