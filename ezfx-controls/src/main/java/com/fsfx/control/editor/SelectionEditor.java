package com.fsfx.control.editor;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

import java.util.List;

public class SelectionEditor<T> extends EditorControl<T> {

	private final ObservableList<T> options;

	public SelectionEditor(Property<T> property, ObservableList<T> options) {
		super(property);
		this.options = options;
	}
	public SelectionEditor(Property<T> property, List<T> options) {
		super(property);
		this.options = FXCollections.observableArrayList(options);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ComboBoxSkin();
	}

	private class ComboBoxSkin extends SkinBase<SelectionEditor<T>> {
		private ComboBoxSkin() {
			super(SelectionEditor.this);
			ComboBox<T> comboBox = new ComboBox<>(options);
			comboBox.valueProperty().bindBidirectional(getProperty());
			getChildren().setAll(comboBox);

		}
	}
}
