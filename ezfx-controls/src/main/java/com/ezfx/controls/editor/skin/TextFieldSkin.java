package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.standard.StringEditor;
import javafx.scene.control.TextField;

public class TextFieldSkin extends StringEditorSkin {
	public TextFieldSkin(StringEditor control) {
		super(control, new TextField());
	}
}