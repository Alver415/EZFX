package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.TextFieldSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Skin;

public class StringEditor extends ObjectEditor<String> {

	public StringEditor() {
		this(new SimpleStringProperty());
	}

	public StringEditor(Property<String> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TextFieldSkin(this);
	}

	private final StringProperty promptText = new SimpleStringProperty(this, "promptText");

	public StringProperty promptTextProperty() {
		return this.promptText;
	}

	public String getPromptText() {
		return this.promptTextProperty().getValue();
	}

	public void setPromptText(String value) {
		this.promptTextProperty().setValue(value);
	}

}
