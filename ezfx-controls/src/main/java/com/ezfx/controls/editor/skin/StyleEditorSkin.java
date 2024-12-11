package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.javafx.StyleEditor;
import javafx.scene.control.TextArea;

public class StyleEditorSkin extends StringEditorSkin {
	public StyleEditorSkin(StyleEditor control) {
		super(control, new TextArea());
	}
}