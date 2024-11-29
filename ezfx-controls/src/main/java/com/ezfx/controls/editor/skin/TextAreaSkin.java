package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.standard.StringEditor;
import javafx.scene.control.TextArea;

public class TextAreaSkin extends StringEditorSkin {
	public TextAreaSkin(StringEditor control) {
		super(control, new TextArea());
	}
}