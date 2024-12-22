package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.standard.BooleanEditor;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

public class CheckBoxSkin extends BooleanEditorSkinBase {
	public CheckBoxSkin(BooleanEditor control) {
		super(control);
		CheckBox checkBox = new CheckBox();
		checkBox.selectedProperty().bindBidirectional(control.valueProperty());
		HBox hBox = new HBox(checkBox);
		getChildren().setAll(hBox);
	}
}