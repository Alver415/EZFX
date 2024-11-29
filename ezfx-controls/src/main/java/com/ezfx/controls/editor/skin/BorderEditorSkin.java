package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.javafx.BorderEditor;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;

public class BorderEditorSkin extends EditorSkin<BorderEditor, Border> {

	public BorderEditorSkin(BorderEditor control) {
		super(control);

		getChildren().setAll(new Label("NOT YET IMPLEMENTED"));
	}
}