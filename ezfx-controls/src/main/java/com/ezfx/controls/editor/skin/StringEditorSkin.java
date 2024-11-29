package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.standard.StringEditor;
import javafx.scene.control.TextInputControl;

public class StringEditorSkin extends EditorSkin<StringEditor, String> {
	public StringEditorSkin(StringEditor control, TextInputControl inputControl) {
		super(control);
		inputControl.textProperty().bindBidirectional(control.property());
		getChildren().setAll(inputControl);
	}
}


