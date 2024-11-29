package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.standard.SelectionEditor;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class ComboBoxSelectionEditorSkin<T> extends EditorSkin<SelectionEditor<T>, T> {

	public ComboBoxSelectionEditorSkin(SelectionEditor<T> control) {
		super(control);
		ComboBox<T> comboBox = new ComboBox<>();
		comboBox.converterProperty().bindBidirectional(control.converterProperty());
		comboBox.itemsProperty().bindBidirectional(control.itemsProperty());
		comboBox.cellFactoryProperty().bindBidirectional(control.cellFactoryProperty());
		comboBox.buttonCellProperty().bindBidirectional(control.buttonCellProperty());
		comboBox.valueProperty().bindBidirectional(control.property());

		HBox hBox = new HBox(comboBox);
		getChildren().setAll(hBox);
	}
}
