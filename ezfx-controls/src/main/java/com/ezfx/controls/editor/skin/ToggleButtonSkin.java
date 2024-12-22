package com.ezfx.controls.editor.skin;
import com.ezfx.controls.editor.impl.standard.BooleanEditor;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

public class ToggleButtonSkin extends BooleanEditorSkinBase {
	public ToggleButtonSkin(BooleanEditor control) {
		super(control);
		ToggleButton toggleButton = new ToggleButton();
		toggleButton.textProperty().bind(toggleButton.selectedProperty().map(String::valueOf));
		toggleButton.selectedProperty().bindBidirectional(control.valueProperty());
		HBox hBox = new HBox(toggleButton);
		getChildren().setAll(hBox);
	}
}