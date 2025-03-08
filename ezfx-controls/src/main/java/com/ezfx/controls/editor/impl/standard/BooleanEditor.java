package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

import java.util.Map;

public class BooleanEditor extends ObjectEditor<Boolean> {

	public BooleanEditor() {
		this(new SimpleBooleanProperty(false));
	}

	public BooleanEditor(Property<Boolean> property) {
		super(property);
		getKnownValues().putAll(Map.of(
				"True", Boolean.TRUE,
				"False", Boolean.FALSE));

	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CheckBoxSkin(this);
	}

	public static class CheckBoxSkin extends EditorSkinBase<BooleanEditor, Boolean> {
		public CheckBoxSkin(BooleanEditor control) {
			super(control);
			CheckBox checkBox = new CheckBox();
			checkBox.selectedProperty().bindBidirectional(control.valueProperty());
			HBox hBox = new HBox(checkBox);
			getChildren().setAll(hBox);
		}
	}

	public static class ToggleButtonSkin extends EditorSkinBase<BooleanEditor, Boolean> {
		public ToggleButtonSkin(BooleanEditor control) {
			super(control);
			ToggleButton toggleButton = new ToggleButton();
			toggleButton.textProperty().bind(toggleButton.selectedProperty().map(String::valueOf));
			toggleButton.selectedProperty().bindBidirectional(control.valueProperty());
			HBox hBox = new HBox(toggleButton);
			getChildren().setAll(hBox);
		}
	}

}
