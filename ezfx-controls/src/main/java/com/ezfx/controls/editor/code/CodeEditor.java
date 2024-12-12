package com.ezfx.controls.editor.code;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.editor.skin.TextFieldSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.shape.Rectangle;


public class CodeEditor extends StringEditor {

	public CodeEditor() {
		this(new SimpleStringProperty());
	}

	public CodeEditor(Property<String> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CodeEditorSkin(this);
	}

}